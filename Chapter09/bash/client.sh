#!/usr/bin/env bash


function createCustomer {
  if [[ $# -ne 1 ]]; then
    echo "Usage: ./client.sh cc customer_name"
    exit 1
  else
    curl -vX POST http://localhost:8888/customer \
      -d "{\"name\": \"$1\"}"
    echo
  fi
}

function placeOrder {
  if [[ $# -ne 2 ]]; then
    echo "Usage: ./client.sh po customer_id good_id"
    exit 1
  else
    curl -vX POST http://localhost:8888/order \
      --cookie "shop_customer_id=$1" \
      -d "{\"good\": $2}"
    echo
  fi
}

function listOrders {
  curl -vX GET http://localhost:8888/order
  echo
}

function listGoods {
  curl -vX GET http://localhost:8888/good
  echo
}

function help {
  echo "Usage: ./client.sh command [arguments]
Possible commands:
* cc - create customer
* po - place order
* lo - list orders
* lg - list goods "
}

case $1 in
  cc) createCustomer "${@:2}";;
  po) placeOrder     "${@:2}";;
  lo) listOrders     "${@:2}";;
  lg) listGoods      "${@:2}";;
   *) help;;
esac
