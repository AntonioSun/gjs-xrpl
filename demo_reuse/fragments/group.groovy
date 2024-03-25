fragment {
    group name: vf_name, loops: vf_loops, users: vf_users, duration: vf_duration,
      keepUser: false, delayedStart: true, scheduler: true, {
        cookies(name: 'cookies manager')

        // insert login fragment
        insert 'fragments/login.groovy'

        http 'GET /api/books', {
            params values: [ limit: '10' ]
        }
    }
}