    fragment {
      debug '--== Tx: fee ==--', displayJMeterVariables: true, displayJMeterProperties: true, enabled: false
      transaction('Tx01 fee', generate: true) {

        http (method: 'POST', path: '/', name: 'Tx01r fee',
              comments: 'https://xrpl.org/fee.html') {
          body '''{"method":"fee","params": [{}]}'''
        }

      }

      flow (name: 'Pace Time Flow Control') {
        uniform_timer (name: 'Pace Time', delay: '${c_pt_delay}', range: '${c_tt_range}')
      }
    }
