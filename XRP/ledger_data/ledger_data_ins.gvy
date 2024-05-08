  fragment {
    group name: vf_name, enabled: vf_enabled, loops: vf_loops, users: vf_users, duration: vf_duration, rampUp: vf_rampUp,
      delay: vf_delay, keepUser: false, delayedStart: true, scheduler: true, {
 
      // Defines a 'Random Variable' config element
      random name: 'Random ledger index number', variable: 'ledgerIndex', minimum: 30000000, maximum: 85478162, perUser: true

      debug '--== Tx: ledger_data ==--', displayJMeterVariables: true, displayJMeterProperties: true, enabled: false
      // transaction('Tx01 ledger_data', generate: true) {
        
        http (method: 'POST', path: '/', name: 'Tx01r ledger_data') {
          body '''{"method":"ledger_data","params": [{"ledger_index":"${ledgerIndex}","binary":false}]}'''
          //extract_jmes expression: 'book.id', variable: 'p_bookId'
        }
    
      // }

      // flow (name: 'Think Time Flow Control', enabled: false) {
      //   uniform_timer (name: 'Think Time', delay: '${c_tt_delay}', range: '${c_tt_range}')
      // }

      flow (name: 'Pace Time Flow Control') {
        uniform_timer (name: 'Pace Time', delay: vf_pt_delay, range: vf_pt_range)
      }

    }
  }
