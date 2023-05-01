const DEFAULTS = {
    endpoint: {
      auth: {
        issueNewToken: 'http://authentication-server:8901/auth/oauth/token'
      },
      users: {
        getById: '/api/users',
        getAll: '/api/users',
        create: '/api/users',
        update: '/api/users',
        delete: '/api/users',
      },
      roles: {
        getAll: '/api/roles',
        create: '/api/roles',
        update: '/api/roles',
        delete: '/api/roles'
      },
      images: {
        uploadImage: 'http://localhost:3000/images',
        getImage: 'http://localhost:3000/images'
      }
    },
    api: {
      host: 'NO_CONFIG',
      rpcHost: 'NO_CONFIG',         
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