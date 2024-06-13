  fragment {
    check_response applyTo: 'children', {
      text() excludes ':\\"telINSUF_FEE_P\\",'
    }

    group name: vf_name, enabled: vf_enabled, loops: vf_loops, users: vf_users, duration: vf_duration,
      rampUp: vf_rampUp, delay: vf_delay, keepUser: false, delayedStart: true, scheduler: true, {

      // Defines a 'Random Variable' config element
      //random name: 'Random ledger index number', variable: 'ledgerIndex', minimum: 30000000, maximum: 85478162, perUser: true

      debug '--== Tx: Tx_TicketCreate1 ==--', displayJMeterVariables: true, displayJMeterProperties: true, enabled: false
      transaction('Tx01 Tx_TicketCreate1', generate: true) {
        
        http (method: 'POST', path: '/', name: 'Tx01r Tx_TicketCreate1',
              comments: 'https://xrpl.org/') {
          body '''{
  "method": "submit",
  "params": [
    {
      "offline": false,
      "secret": "${s_secret}",
      "fail_hard": true,
      "tx_json": {
        "TransactionType": "TicketCreate",
        "Account": "${s_account}",
        "TicketCount": ${p_counts},
        "Fee": "${p_tx_fee}"
      }
    }
  ]
}'''
          extract_jmes expression: 'result.error', variable: 'p_error'
          extract_jmes expression: 'result.engine_result', variable: 'p_result'
        }
      }

     flow (name: 'Pace Time Flow Control') {
        uniform_timer (name: 'Pace Time', delay: vf_pt_delay, range: vf_pt_range)
      }

    }
  }
