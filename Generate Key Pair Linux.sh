# Generate key pair, encrypts them by password and write them into PEM file
openssl genrsa -des3 -out private.pem 2048

# Extract public key from above key pair file
openssl rsa -in private.pem -outform PEM -pubout -out public.pem