  fragment {
    check_response {
      text() excludes ':\\"telINSUF_FEE_P\\",'
    }

    group name: vf_name, enabled: vf_enabled, loops: vf_loops, users: vf_users, duration: vf_duration,
      rampUp: vf_rampUp, delay: vf_delay, keepUser: false, delayedStart: true, scheduler: true, {

      // Defines a 'Random Variable' config element
      //random name: 'Random ledger index number', variable: 'ledgerIndex', minimum: 30000000, maximum: 85478162, perUser: true

      debug '--== Tx: Tx_TicketCreate ==--', displayJMeterVariables: true, displayJMeterProperties: true, enabled: false
      transaction('Tx01 Tx_TicketCreate 1', generate: true) {
        
        http (method: 'POST', path: '/', name: 'Tx01r Tx_TicketCreate 1',
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
        "TicketCount": 10,
        "Fee": "${p_tx_fee}"
      }
    }
  ]
}'''
          extract_jmes expression: 'result.engine_result', variable: 'p_result'
        }
      }
      flow (name: 'Think Time Flow Control') {
        uniform_timer (name: 'Think Time', delay: '${c_tt_delay}', range: '${c_tt_range}')
      }

      // -----------------------------
      // transaction Tx_TicketCreate 2
      transaction('Tx01 Tx_TicketCreate 2', generate: true) {
        
        http (method: 'POST', path: '/', name: 'Tx01r Tx_TicketCreate 2',
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
        "TicketCount": 20,
        "Fee": "${p_tx_fee}"
      }
    }
  ]
}'''
          extract_jmes expression: 'result.engine_result', variable: 'p_result'
        }
      }
      flow (name: 'Think Time Flow Control') {
        uniform_timer (name: 'Think Time', delay: '${c_tt_delay}', range: '${c_tt_range}')
      }

      // -----------------------------
      // transaction Tx_TicketCreate 3
      transaction('Tx01 Tx_TicketCreate 3', generate: true) {
        
        http (method: 'POST', path: '/', name: 'Tx01r Tx_TicketCreate 3',
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
        "TicketCount": 30,
        "Fee": "${p_tx_fee}"
      }
    }
  ]
}'''
          extract_jmes expression: 'result.engine_result', variable: 'p_result'
        }
      }
      flow (name: 'Think Time Flow Control') {
        uniform_timer (name: 'Think Time', delay: '${c_tt_delay}', range: '${c_tt_range}')
      }

      // -----------------------------
      // transaction Tx_TicketCreate 5
       transaction('Tx02 Tx_TicketCreate x2', generate: true) {
        loop(count: 2) {
          http (method: 'POST', path: '/', name: 'Tx02r Tx_TicketCreate x2',
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
          "TicketCount": 10,
          "Fee": "${p_tx_fee}"
        }
      }
    ]
  }'''
          }
        }
      }

     flow (name: 'Pace Time Flow Control') {
        uniform_timer (name: 'Pace Time', delay: vf_pt_delay, range: vf_pt_range)
      }

    }
  }
