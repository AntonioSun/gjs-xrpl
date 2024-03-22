    fragment {
      debug '--== Tx: ledger_closed ==--', displayJMeterVariables: true, displayJMeterProperties: true, enabled: false
      transaction('Tx01 ledger_closed', generate: true) {

        http (method: 'POST', path: '/', name: 'Tx01r ledger_closed',
              comments: 'https://xrpl.org/ledger_closed.html') {
          body '''{"method":"ledger_closed","params": [{}]}'''
        }

      }

      flow (name: 'Pace Time Flow Control') {
        uniform_timer (name: 'Pace Time', delay: '${c_pt_delay}', range: '${c_tt_range}')
      }
    }
