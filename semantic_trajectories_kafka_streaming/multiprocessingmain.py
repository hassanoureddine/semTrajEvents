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
#server = ["localhost:9093", "localhost:9094"] #broker1 broker2
server = "localhost:9093" #broker1

# data files path
#path = 'data without redundant rows'
#path = 'one trajectory'
path = 'semantic trajectories'


# kafka topic
topic = 'semTraj'

# create a Kafka producer with json serializer
producer = KafkaProducer(value_serializer=lambda v: json.dumps(v).encode('utf-8'), bootstrap_servers=server)


def produce_stream(df):
    #df = pd.read_csv(path + "\\" + file, low_memory=False)

    df = df.replace({"\"": ''}, regex=True)  # to remove " in values that causes issues for decerialising to json on flink
    df = df.replace({";75116": ''}, regex=True)  # to remove ; in values that causes issues for decerialising to json on flink (e.g., 75016;75116)
    df = df.replace({";75017": ''},  regex=True)  # to remove ; in values that causes issues for decerialising to json on flink (e.g., 75016;75017)
    df = df.replace({";75010": ''},regex=True)  # to remove ; in values that causes issues for decerialising to json on flink (e.g., 75009;75010)
    df = df.replace({"92 100": '92100'}, regex=True)

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
            #with open(logFile, 'w') as f:
                #f.write('The number of streaming rate for ' + str(seconds) + ' is ' + str(nb_of_streamed_events))

            print('The number of streaming rate for ' + logFile + ' is ' + str(nb_of_streamed_events) + ' per ' + str(seconds))
            nb_of_streamed_events = 0
            start = time.time()


        #producer.flush()

        #print(trajectory_segment)
        #sleep(1)


def main():
    print("*** Starting semantic trajectories stream on " + str(server) + ", topic : " + topic)

    try:
        producer.flush()
        #df = pd.read_csv('all.csv', low_memory=False)
        #size_df = len(df)
        #split_size = int(size_df/4)
        #print(str(size_df))
        #print(str(split_size))
        #df1 = df.iloc[:split_size, :]
        #df2 = df.iloc[split_size + 1: split_size + split_size, :]
        #df3 = df.iloc[split_size + split_size + 1: split_size + split_size + split_size, :]
        #df4 = df.iloc[split_size + split_size + split_size + 1:, :]

        #df1.to_csv(path + "/" + "df1.csv", index=False)
        #df2.to_csv(path + "/" + "df2.csv", index=False)
        #df3.to_csv(path + "/" + "df3.csv", index=False)
        #df4.to_csv(path + "/" + "df4.csv", index=False)

        df1 = pd.read_csv(path + "/" + "df1.csv", low_memory=False)
        df2 = pd.read_csv(path + "/" + "df2.csv", low_memory=False)
        df3 = pd.read_csv(path + "/" + "df3.csv", low_memory=False)
        df4 = pd.read_csv(path + "/" + "df4.csv", low_memory=False)

        

        

        process1 = multiprocessing.Process(target=produce_stream, args=[df1])
        process2 = multiprocessing.Process(target=produce_stream, args=[df2])
        process3 = multiprocessing.Process(target=produce_stream, args=[df3])
        process4 = multiprocessing.Process(target=produce_stream, args=[df4])

        process1.start()
        process2.start()
        process3.start()
        process4.start()

        process1.join()
        process2.join()
        process3.join()
        process4.join()

    except KeyboardInterrupt:
        pass

    


if __name__ == '__main__':
    main()
