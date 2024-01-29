class load_settings {

    //def static tusers  =  System.env['TEST_USERS'] ?: 1
    def static tramp  =  System.env['TEST_RAMP'] ?: 10
    def static tduration = System.env['TEST_DURATION'] ?: 120

    def static v = [
        server_info: [ users: 1, ramp: tramp, delay: 0, loops: 1, duration: tduration, scheduler: true, enabled: true ],
        account_info: [ users: 1, ramp: tramp, delay: 0, loops: 1, duration: tduration, scheduler: true, enabled: true ],
        nft_info: [ users: 1, ramp: tramp, delay: 0, loops: 1, duration: tduration, scheduler: true, enabled: true ],
        ledger_data: [ users: 1, ramp: tramp, delay: 0, loops: 1, duration: tduration, scheduler: true, enabled: true ],
    ]
}
