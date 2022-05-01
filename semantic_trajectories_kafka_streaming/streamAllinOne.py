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
import sys

# broker server
#server = "localhost:9092" #broker
#server = ["localhost:9093", "localhost:9094"] #broker1 broker2
server = "localhost:9093" #broker1


# data files path
path = 'semantic trajectories'

# kafka topic
topic = 'semTraj'

# create a Kafka producer with json serializer
producer = KafkaProducer(value_serializer=lambda v: json.dumps(v).encode('utf-8'),  bootstrap_servers=server) 

def produce_stream(df):
    df = df.replace({"\"": ''}, regex=True) #to remove " in values that causes issues for decerialising to json on flink
    df = df.replace({";75116": ''}, regex=True) #to remove ; in values that causes issues for decerialising to json on flink (e.g., 75016;75116)
    df = df.replace({";75017": ''}, regex=True)  # to remove ; in values that causes issues for decerialising to json on flink (e.g., 75016;75017)

    nb_of_streamed_events = 0
    start = time.time()
    start1 = time.time()
    for index, row in df.iterrows():
        trajectory_segment = row.to_json()
        print(str(sys.getsizeof(trajectory_segment)))

        ack = producer.send(topic, trajectory_segment)
        #metadata = ack.get()
        #print(metadata.topic)
        #print(metadata.partition)

        ### To calculate events per seconds
        nb_of_streamed_events = nb_of_streamed_events + 1
        end = time.time()
        seconds = end - start
        if seconds >= 1:
            with open('streamingRate.txt', 'w') as f:
                f.write('The number of streaming rate for ' + str(seconds) + ' is ' + str(nb_of_streamed_events))
            nb_of_streamed_events = 0
            start = time.time()
        ### End to calculate events per seconds

        print(trajectory_segment)
        #sleep(1)
        
    end1 = time.time()
    seconds1 = end1 - start1
    print("====" + str(seconds1) + "====")


def produce_stream_2(df):
    df = df.replace({"\"": ''}, regex=True)  # to remove " in values that causes issues for decerialising to json on flink
    df = df.replace({";75116": ''}, regex=True)  # to remove ; in values that causes issues for decerialising to json on flink (e.g., 75016;75116)
    df = df.replace({";75017": ''}, regex=True)  # to remove ; in values that causes issues for decerialising to json on flink (e.g., 75016;75017)
    df = df.replace({";75010": ''}, regex=True)  # to remove ; in values that causes issues for decerialising to json on flink (e.g., 75009;75010)
    df = df.replace({"92 100": '92100'}, regex=True)

    nb_of_streamed_events = 0
    start = time.time()
    for index in range(1, df.shape[0]):
        trajectory_segment1 = df.iloc[index].to_json()
        print(trajectory_segment1)


        ### To calculate events per seconds
        nb_of_streamed_events = nb_of_streamed_events + 1
        end = time.time()
        seconds = end - start
        if seconds >= 1:
            with open('streamingRate.txt', 'w') as f:
                f.write('The number of streaming rate for ' + str(seconds) + ' is ' + str(nb_of_streamed_events))
            nb_of_streamed_events = 0
            start = time.time()
        ### End to calculate events per seconds




def main():
    # read the folder where you stored the users raw csvs data and store then in a list called input_files
    input_files = [f for f in os.listdir(path) if f.endswith(('.csv', '.CSV'))]

    allDfs = []
    #for file in input_files:
        #allDfs.append(pd.read_csv(path + "\\" + file, low_memory=False))

    #toStreamDF = pd.concat(allDfs)
    #toStreamDF = toStreamDF.sort_values(by='start_datetime')

    #toStreamDF.to_csv('all.csv', index=False)
    #produce_stream(toStreamDF)

    #produce_stream(pd.read_csv('all.csv', low_memory=False))
    produce_stream(pd.read_csv('112-117.csv', low_memory=False))

    #produce_stream(pd.read_csv('112-117.csv', low_memory=False))

if __name__ == '__main__':
    main()
