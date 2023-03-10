HOST_NAME=phucsinh.hopto.org

openssl req -newkey rsa:2048 -sha256 -nodes -keyout ca.key -x509 -days 365 -out $HOST_NAME.pub.pem -subj "/C=VN/ST=Ho Chi Minh/L=HCM/O=Swiss Post Solutions/CN=$HOST_NAME"
openssl x509 -text -noout -in $HOST_NAME.pub.pem
openssl pkcs12 -export -in $HOST_NAME.pub.pem -inkey ca.key -out $HOST_NAME.p12 -name $HOST_NAME

rm ca.key