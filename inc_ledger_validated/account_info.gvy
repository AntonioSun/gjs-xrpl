    fragment {
      debug '--== Tx: account_info ==--', displayJMeterVariables: true, displayJMeterProperties: true, enabled: false
      transaction('Tx01 account_info', generate: true) {

        http (method: 'POST', path: '/', name: 'Tx01r account_info',
              comments: 'https://xrpl.org/account_info.html') {
          body '''{"method":"account_info","params": [{"account":"${s_account}","ledger_index":"validated","queue":false}]}'''
        }

      }
      flow (name: 'Pace Time Flow Control') {
        uniform_timer (name: 'Pace Time', delay: '${c_pt_delay}', range: '${c_tt_range}')
      }
    }
