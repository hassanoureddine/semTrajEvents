import multiprocessing

from kafka import KafkaProducer
from multiprocessing import Pool
import os
import time
import pandas as pd
from random import gauss
from time import sleep
import sys
import json
import requests as req

# broker server
#server = "localhost:9092" #broker
server = ["localhost:9093", "localhost:9094"] #broker1 broker2
#server = "localhost:9093" #broker1

# data files path
path = 'semantic trajectories'

# kafka topic
topic = 'semTraj'

# create a Kafka producer with json serializer
producer = KafkaProducer(value_serializer=lambda v: json.dumps(v).encode('utf-8'), bootstrap_servers=server)




def produce_stream(file):
    df = pd.read_csv(path + "\\" + file, low_memory=False)

    df = df.replace({"\"": ''}, regex=True)  # to remove " in values that causes issues for decerialising to json on flink
    df = df.replace({";75116": ''}, regex=True)  # to remove ; in values that causes issues for decerialising to json on flink (e.g., 75016;75116)
    df = df.replace({";75017": ''},  regex=True)  # to remove ; in values that causes issues for decerialising to json on flink (e.g., 75016;75017)

    start = time.time()
    nb_of_streamed_events = 0
    for index, row in df.iterrows():
        trajectory_segment = row.to_json()

        producer.send(topic, trajectory_segment)
        nb_of_streamed_events = nb_of_streamed_events + 1
        end = time.time()
        seconds = end - start
        logFile = str(multiprocessing.current_process().name) + '.txt'
        if seconds >= 1:
            with open(logFile, 'w') as f:
                f.write('The number of streaming rate for ' + str(seconds) + ' is ' + str(nb_of_streamed_events))
            nb_of_streamed_events = 0
            start = time.time()


        #producer.flush()

        print(trajectory_segment)
        #sleep(1)


def main():
    print("*** Starting semantic trajectories stream on " + str(server) + ", topic : " + topic)

    try:
        # read the folder where you stored the users raw csvs data and store then in a list called input_files
        input_files = [f for f in os.listdir(path) if f.endswith(('.csv', '.CSV'))]

        p = Pool(5)
        i = 0
        print('# files', len(input_files))
        print(str(i) + ' out of ' + str(len(input_files)))

        global_var = 0
        start = time.time()

        for _ in p.imap_unordered(produce_stream, input_files, chunksize=1):
            i += 1
            print('# files', len(input_files))
            print(str(i) + ' out of ' + str(len(input_files)))

        print("FINISHED !!")

        p.close()
        p.join()

    except KeyboardInterrupt:
        pass

    


if __name__ == '__main__':
    main()
