-- Drop table

-- DROP TABLE public.customer

CREATE TABLE customer (
  id serial NOT NULL,
  "name" varchar NOT NULL,
  CONSTRAINT customer_pk PRIMARY KEY (id),
  CONSTRAINT customer_un UNIQUE (name)
)
WITH (
  OIDS=FALSE
) ;
CREATE UNIQUE INDEX customer_name_idx ON public.customer USING btree (name) ;

-- Drop table

-- DROP TABLE public.good

CREATE TABLE good (
  id serial NOT NULL,
  "name" varchar NOT NULL,
  price float4 NOT NULL,
  stock int4 NOT NULL DEFAULT 0,
  CONSTRAINT good_pk PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
) ;

-- Drop table

-- DROP TABLE public."order"

CREATE TABLE "order" (
  id serial NOT NULL,
  customer int4 NOT NULL,
  time_placed timestamp NOT NULL DEFAULT now(),
  good int4 NOT NULL,
  CONSTRAINT store_order_pk PRIMARY KEY (id),
  CONSTRAINT order_customer_fk FOREIGN KEY (customer) REFERENCES customer(id) ON DELETE CASCADE,
  CONSTRAINT order_good_fk FOREIGN KEY (good) REFERENCES good(id) ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
) ;
