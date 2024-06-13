    fragment {

      debug '--== Tx: account_objects ==--', displayJMeterVariables: true, displayJMeterProperties: true, enabled: false
      transaction('Tx01 account_objects', generate: true) {
        
        http (method: 'POST', path: '/', name: 'Tx01r account_objects',
              comments: 'https://xrpl.org/account_objects.html') {
          body '''{"method":"account_objects","params": [{"account":"${s_account}","ledger_index":"validated","limit": 300}]}'''
          extract_jmes expression: 'result.error', variable: 'p_error'
          extract_jmes expression: 'length(result.account_objects)', variable: 'p_objects'
        }
      }

      // flow (name: 'Think Time Flow Control', enabled: false) {
      //   uniform_timer (name: 'Think Time', delay: '${c_tt_delay}', range: '${c_tt_range}')
      // }

      flow (name: 'Pace Time Flow Control') {
        uniform_timer (name: 'Pace Time', delay: vf_pt_delay, range: vf_pt_range)
      }

    }
