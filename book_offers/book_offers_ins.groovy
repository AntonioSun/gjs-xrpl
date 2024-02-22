
fragment {
    simple {

      // Defines a 'Random Variable' config element
      random name: 'Random ledger index number', variable: 'ledgerIndex', minimum: 30000000, maximum: 85478162, perUser: true

      debug '--== Tx: book_offers ==--', displayJMeterVariables: true, displayJMeterProperties: true, enabled: false
      transaction('Tx01 book_offers', generate: true) {
        
        http (method: 'POST', path: '/', name: 'Tx01r book_offers, random ledger') {
          body '''{"method":"book_offers","params": [{"ledger_index": "${ledgerIndex}","taker": "${acct}","taker_gets": {"currency": "${vCurrency}","issuer": "${vIssuer}"},"taker_pays": {"currency": "XRP"}}]}'''
          //extract_jmes expression: 'book.id', variable: 'p_bookId'
        }
    
      }

      flow (name: 'Think Time Flow Control', enabled: false) {
        uniform_timer (name: 'Think Time', delay: '${c_tt_delay}', range: '${c_tt_range}')
      }

      flow (name: 'Pace Time Flow Control') {
        uniform_timer (name: 'Pace Time', delay: '${c_tt_delay}', range: '${c_tt_range}')
      }

    }
}
