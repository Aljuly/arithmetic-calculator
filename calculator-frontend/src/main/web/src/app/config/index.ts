const DEFAULTS = {
    endpoint: {
      auth: {
        //issueNewToken: 'http://authentication-server:8901/auth/oauth/token'
        issueNewToken: '/v1.0/login'
      },
      users: {
        getById: '/v1.0/users',
        getAll: '/v1.0/users',
        create: '/v1.0/users',
        update: '/v1.0/users',
        delete: '/v1.0/users',
        email:  '/v1.0/users',
        name:  '/v1.0/users',
      },
      roles: {
        getAll: '/v1.0/roles',
        create: '/v1.0/roles',
        update: '/v1.0/roles',
        delete: '/v1.0/roles'
      },
      images: {
        uploadImage: '/v1.0/images/',
        getImage: '/v1.0/images/'
      }
    },
    api: {
      protocol: 'http',
      host: 'localhost',
      port: '8080',
      //port: '4242',
      tradeReport: 'NO_CONFIG',
      microServiceHost: 'baseProtocol://baseDomain'
    },
    reports: {
      server: 'NO_CONFIG',
      auth: {
        login: 'NO_CONFIG',
        password: 'NO_CONFIG'
      },
      instances: {
        performanceReturn: 'NO_CONFIG'
      }
    },
    login: {
      host: 'NO_CONFIG'
    },
    claims: ['NO_CONFIG'],
    defaultCurrencyPriorities: ['NO_CONFIG']
  };
  const config = DEFAULTS;
  const ENV = process.env["NODE_ENV"] || 'development';
  if (ENV === 'development') {
    const API_BASE_PROTOCOL = 'http';
    const API_BASE_DOMAIN = 'localhost';
    config.api.microServiceHost = config.api.microServiceHost
      .replace('baseDomain', API_BASE_DOMAIN)
      .replace('baseProtocol', API_BASE_PROTOCOL);
  }
  export default config;
