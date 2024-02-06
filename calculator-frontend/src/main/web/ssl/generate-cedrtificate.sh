util_url=https://github.com/RubenVermeulen/generate-trusted-ssl-certificate.git
util_dir=generate-trusted-ssl-certificate

git clone ${util_url}
cp openssl-custom.cnf ./${util_dir}
cd ${util_dir}
bash generate.sh
mv server.* ../
cd ..
rm -rf ${util_dir}
