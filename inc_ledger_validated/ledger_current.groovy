    fragment {
      debug '--== Tx: ledger_current ==--', displayJMeterVariables: true, displayJMeterProperties: true, enabled: false
      transaction('Tx01 ledger_current', generate: true) {

        http (method: 'POST', path: '/', name: 'Tx01r ledger_current',
              comments: 'https://xrpl.org/ledger_current.html') {
          body '''{"method":"ledger_current","params": [{}]}'''
        }

      }

      flow (name: 'Pace Time Flow Control') {
        uniform_timer (name: 'Pace Time', delay: '${c_pt_delay}', range: '${c_tt_range}')
      }
    }
