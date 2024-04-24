    fragment {

      debug '--== Tx: account_info ==--', displayJMeterVariables: true, displayJMeterProperties: true, enabled: false
      transaction('Tx01 account_info', generate: true) {
        
        http (method: 'POST', path: '/', name: 'Tx01r account_info',
              comments: 'https://xrpl.org/account_info.html') {
          body '''{"method":"account_info","params": [{"account":"${s_account}","ledger_index":"validated","queue":false}]}'''
          extract_jmes expression: 'result.error', variable: 'p_error'
          extract_jmes expression: 'result.account_data.Balance', variable: 'p_result'
        }
      }

      flow (name: 'Think Time Flow Control', enabled: false) {
        uniform_timer (name: 'Think Time', delay: '${c_tt_delay}', range: '${c_tt_range}')
      }

      flow (name: 'Pace Time Flow Control') {
        uniform_timer (name: 'Pace Time', delay: '${c_pt_delay}', range: '${c_pt_range}')
      }

    }
