  fragment {

    group name: vf_name, loops: vf_loops, users: vf_users, duration: vf_duration,
      rampUp: vf_rampUp, delay: vf_delay, keepUser: false, delayedStart: true, scheduler: true, {

      csv name: 'CSV '+ vf_csv, file: vf_csv, variables: ["s_account","s_secret"], recycle: false, stopUser: true

      // Defines a 'Random Variable' config element
      //random name: 'Random ledger index number', variable: 'ledgerIndex', minimum: 30000000, maximum: 85478162, perUser: true

      debug '--== Tx: Tx_AccountDelete ==--', displayJMeterVariables: true, displayJMeterProperties: true, enabled: false
      transaction('Tx01 Tx_AccountDelete', generate: true) {
        
        http (method: 'POST', path: '/', name: 'Tx01r Tx_AccountDelete',
              comments: 'https://xrpl.org/') {
          body '''{
  "method": "submit",
  "params": [
    {
      "offline": false,
      "secret": "${s_secret}",
      "fail_hard": true,
      "tx_json": {
        "TransactionType": "AccountDelete",
        "Account": "${s_account}",
        "Destination": "${p_acct_dest}",
        "Fee": "${p_del_fee}"
      }
    }
  ]
}'''
          extract_jmes expression: 'result.engine_result', variable: 'p_result'
        }
    
      }

      flow (name: 'Think Time Flow Control', enabled: false) {
        uniform_timer (name: 'Think Time', delay: '${c_tt_delay}', range: '${c_tt_range}')
      }

      flow (name: 'Pace Time Flow Control') {
        uniform_timer (name: 'Pace Time', delay: vf_pt_delay, range: vf_pt_range)
      }

    }
  }
