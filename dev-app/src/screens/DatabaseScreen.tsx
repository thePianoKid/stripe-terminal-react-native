import React, { useContext } from 'react';
import { Platform, ScrollView, StyleSheet, Text } from 'react-native';
import type { OfflineStatus } from 'src/types';
import { colors } from '../colors';
import List from '../components/List';
import ListItem from '../components/ListItem';
import { AppContext } from '../AppContext';
import { useStripeTerminal } from '@stripe/stripe-terminal-react-native';

export default function DatabaseScreen() {
  const { account } = useContext(AppContext);
  let offStatus: OfflineStatus | undefined;
  const currencySymbols = [
    { value: 'USD', label: '$' },
    { value: 'GBP', label: '￡' },
    { value: 'CAD', label: 'C$' },
    { value: 'SGD', label: 'S$' },
    { value: 'EUR', label: '€' },
    { value: 'AUD', label: 'A$' },
    { value: 'NZD', label: 'NZ$' },
    { value: 'DKK', label: 'DKr' },
    { value: 'SEK', label: 'Kr' },
  ];
  const {} = useStripeTerminal({
    onDidChangeOfflineStatus(status: OfflineStatus) {
      console.log('DatabaseScreen onDidChangeOfflineStatus');
      offStatus = status;
    },
  });
  function getCurrencySymbols(currency: string) {
    currencySymbols.map((a) => {
      if (currency === a.value) {
        return a.label;
      }
      return '$';
    });
  }
  return (
    <ScrollView style={styles.container}>
      <List bolded={false} topSpacing={false} title="PUBLIC INTERFACE SUMMARY">
        {offStatus && offStatus.offlinePaymentsCount > 0 ? (
          <ListItem
            title={
              getCurrencySymbols(
                offStatus
                  ? offStatus.offlinePaymentAmountsByCurrency[0].currency
                  : 'USD'
              ) +
              ' ' +
              offStatus?.offlinePaymentAmountsByCurrency[0].amount
            }
          />
        ) : (
          <></>
        )}
      </List>
      <Text style={styles.infoText}>
        {' '}
        {String(offStatus ? offStatus.offlinePaymentsCount : 0) +
          ' payment intent(s) for ' +
          account?.settings?.dashboard.display_name +
          '\nNetwork status: ' +
          (offStatus ? offStatus.networkStatus : 'unknown')}{' '}
      </Text>
      <List bolded={false} topSpacing={false} title="PAYMENT INTENTS">
        {offStatus && offStatus.offlinePaymentsCount > 0 ? (
          offStatus?.offlinePaymentAmountsByCurrency.map((a) => (
            <ListItem
              title={
                getCurrencySymbols(
                  offStatus
                    ? offStatus.offlinePaymentAmountsByCurrency[0].currency
                    : 'USD'
                ) +
                ' ' +
                a.amount
              }
            />
          ))
        ) : (
          <></>
        )}
      </List>
      <Text style={styles.infoText}>
        {' '}
        {String(offStatus ? offStatus.offlinePaymentsCount : 0) +
          ' payment intent(s) for ' +
          account?.settings?.dashboard.display_name}{' '}
      </Text>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: colors.light_gray,
    height: '100%',
    paddingVertical: 22,
  },
  input: {
    height: 44,
    backgroundColor: colors.white,
    paddingLeft: 16,
    marginBottom: 12,
    borderBottomColor: colors.gray,
    color: colors.dark_gray,
    ...Platform.select({
      ios: {
        borderBottomWidth: StyleSheet.hairlineWidth,
      },
      android: {
        borderBottomWidth: 1,
        borderBottomColor: `${colors.gray}66`,
      },
    }),
  },
  infoText: {
    color: colors.dark_gray,
    paddingHorizontal: 16,
    marginVertical: 16,
  },
  buttonWrapper: {
    marginTop: 35,
  },
});
