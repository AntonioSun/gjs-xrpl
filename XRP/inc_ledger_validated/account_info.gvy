  fragment {

    group name: vf_name, enabled: vf_enabled, loops: vf_loops, users: vf_users, duration: vf_duration,
      rampUp: vf_rampUp, delay: vf_delay, keepUser: false, delayedStart: true, scheduler: true, {

      debug '--== Tx: account_info ==--', displayJMeterVariables: true, displayJMeterProperties: true, enabled: false
      transaction('Tx01 account_info', generate: true) {

        http (method: 'POST', path: '/', name: 'Tx01r account_info'+vf_suffix,
              comments: 'https://xrpl.org/account_info.html') {
          body '''{"method":"account_info","params": [{"account":"${s_account}","ledger_index":"${'''+vf_ledger_type+'''}","queue":false}]}'''

          extract_jmes expression: 'result.error', variable: 'p_error'
          extract_jmes expression: 'result.account_data.Balance', variable: 'p_result'
        }

      }
      flow (name: 'Pace Time Flow Control') {
        uniform_timer (name: 'Pace Time', delay: vf_pt_delay, range: vf_pt_range)
      }
    }
  }
