drop Table remote_charges;

create Table remote_charges
(charge_id              INTEGER PRIMARY KEY,
 vehicle_vin            TEXT,
 charging_status        TEXT,
 timestamp              TEXT,
 start_of_charge        TEXT,
 end_of_charge          TEXT,
 quantity_charged       TEXT,
 average_charging_power TEXT,
 soc_start              INTEGER,
 soc_end                INTEGER,
 mobile_charge_id       INTEGER
);
