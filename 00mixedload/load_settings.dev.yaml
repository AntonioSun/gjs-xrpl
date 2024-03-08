class load_settings {

    //def static tusers  =  System.env['TEST_USERS'] ?: 1
    def static tramp  =  System.env['TEST_RAMP'] ?: 10
    def static tduration = System.env['TEST_DURATION'] ?: 120

    def static v = [
        server_info:  [ enabled: true, users: 1, ramp: tramp, delay: 0, loops: 1, duration: tduration, scheduler: true ],
        account_info: [ enabled: true, users: 1, ramp: tramp, delay: 0, loops: 1, duration: tduration, scheduler: true ],
        nft_info:     [ enabled: true, users: 1, ramp: tramp, delay: 0, loops: 1, duration: tduration, scheduler: true ],
        ledger_data:  [ enabled: true, users: 1, ramp: tramp, delay: 0, loops: 1, duration: tduration, scheduler: true ],
    ]
}
