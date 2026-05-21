# Use the official Node.js 16 Alpine image as the base image for building
FROM node:16-alpine AS build

# Set the working directory inside the container
WORKDIR /usr/src/app

# Copy package.json and package-lock.json to the working directory
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application code to the working directory
COPY . .

# Build the Angular application (call ng directly — node:16 uses OpenSSL 1.x,
# which doesn't need --openssl-legacy-provider and rejects it in NODE_OPTIONS)
RUN node_modules/.bin/ng build --prod

# Use the official Nginx image for serving the application
FROM nginx:alpine

# Config setup
COPY nginx/default.conf /etc/nginx/nginx.conf

# Copy the built Angular application to the Nginx HTML directory
COPY --from=build /usr/src/app/dist /usr/share/nginx/html

# Expose the port Nginx will serve on
EXPOSE 80

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]