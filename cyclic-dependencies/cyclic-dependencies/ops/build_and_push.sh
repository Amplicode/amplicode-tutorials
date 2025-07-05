cd .. && docker build . -t orders --platform=linux/amd64

docker tag orders cr.yandex/crpbk8g4u7lsdfqsm38s/orders:$1

docker push cr.yandex/crpbk8g4u7lsdfqsm38s/orders:$1
