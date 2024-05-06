  fragment {
    csv name: 'CSV book_offers', file: '../../common/book_offers.csv', variables: ["acct","vCurrency","vIssuer","ledgerHash","ledgerIndex"]

    group name: vf_name, enabled: vf_enabled, loops: vf_loops, users: vf_users, duration: vf_duration,
      rampUp: vf_rampUp, delay: vf_delay, keepUser: false, delayedStart: true, scheduler: true, {


      // Defines a 'Random Variable' config element
      random name: 'Random ledger index number', variable: 'ledgerIndex', minimum: 30000000, maximum: 85478162, perUser: true

      debug '--== Tx: book_offers ==--', displayJMeterVariables: true, displayJMeterProperties: true, enabled: false
      transaction('Tx01 book_offers', generate: true) {
        
        http (method: 'POST', path: '/', name: 'Tx01r book_offers',
              comments: 'https://xrpl.org/book_offers.html') {
          body '''{"method":"book_offers","params": [{"ledger_index": "${ledgerIndex}","taker": "${acct}","taker_gets": {"currency": "${vCurrency}","issuer": "${vIssuer}"},"taker_pays": {"currency": "XRP"}}]}'''

          extract_jmes expression: 'result.error', variable: 'p_error'
          extract_jmes expression: 'result.engine_result', variable: 'p_result'
        }
    
      }

      flow (name: 'Think Time Flow Control', enabled: false) {
        uniform_timer (name: 'Think Time', delay: '${c_tt_delay}', range: '${c_tt_range}')
      }

      flow (name: 'Pace Time Flow Control') {
        uniform_timer (name: 'Pace Time', delay: '${c_pt_delay}', range: '${c_pt_range}')
      }

    }
  }
