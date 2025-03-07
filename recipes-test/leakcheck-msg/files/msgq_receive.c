/*##############################################################################
 # If not stated otherwise in this file or this component's LICENSE file the
 # following copyright and licenses apply:
 #
 # Copyright 2023 RDK Management
 #
 # Licensed under the Apache License, Version 2.0 (the "License");
 # you may not use this file except in compliance with the License.
 # You may obtain a copy of the License at
 #
 # http://www.apache.org/licenses/LICENSE-2.0
 #
 # Unless required by applicable law or agreed to in writing, software
 # distributed under the License is distributed on an "AS IS" BASIS,
 # WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 # See the License for the specific language governing permissions and
 # limitations under the License.
 ##############################################################################
 */

#define _GNU_SOURCE
#include <mqueue.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <signal.h>
#include <pthread.h>
#include <unistd.h>
#include <fcntl.h>           
#include <sys/stat.h>       
#include <mqueue.h>
#include <sys/types.h>
#include <sanitizer/lsan_interface.h>

void start_thread();
void create_msgq_leak_check();
pthread_mutex_t mq_mutex = PTHREAD_MUTEX_INITIALIZER;

#define MQ_MSG_SIZE 1024

typedef struct mq_msg {
    int seq;
    char message[100];
} mq_msg;


mqd_t mq;

void * thread_start (void *arg) {
    struct mq_attr attr;
    mq_msg msg;
    char queue_name[50];

#define QUEUE_NAME_PREFIX "/mq_leak_"
        int pid = getpid();
        snprintf(queue_name, sizeof(queue_name), "%s%d", QUEUE_NAME_PREFIX, pid);
        printf("Recv Queue name:%s\n",queue_name);

    attr.mq_flags = 0;
    attr.mq_maxmsg = 10;
    attr.mq_msgsize = sizeof(mq_msg);
    attr.mq_curmsgs = 0;

  pthread_mutex_lock(&mq_mutex);
    mq = mq_open(queue_name, O_CREAT | O_RDONLY, 0644, &attr);
   pthread_mutex_unlock(&mq_mutex);
    if (mq < 0) {
        perror("mq_open");
        exit(1);
    }

  while(1) {
    pthread_mutex_lock(&mq_mutex);
    int msg_received = mq_receive(mq, (char*)&msg, sizeof(mq_msg), NULL);
    pthread_mutex_unlock(&mq_mutex);
    if(msg_received == -1) {
        perror("mq_receive");
      break;
    }

    // Display the received message
    printf("Received Message from MQ:%s\n",queue_name);
    if (strcmp(msg.message,"leak_check") == 0) {
           printf("Invoking lsan to detect Memory leak");
            __lsan_do_recoverable_leak_check();
    }
    else {
            printf("Message from MQ:%s is not a leakCheck Msg\n",queue_name);
    }
  }
    // Close the message queue
   pthread_mutex_lock(&mq_mutex);
    mq_close(mq);
    mq_unlink(mq);
    mq = NULL;
  pthread_mutex_unlock(&mq_mutex);
} 
#ifdef MSGQ_CREATE
__attribute__((destructor)) 
#endif
void exit_msgq()
{
  printf("In Destructor to clear the MsgQ\n");
  pthread_mutex_lock(&mq_mutex);
        if (mq) {
                printf("Cleaning  the MsgQ:%d\n",getpid());
                mq_close(mq);
                mq_unlink(mq);
        }
  pthread_mutex_unlock(&mq_mutex);
}

#ifdef MSGQ_CREATE
__attribute__((constructor))
#endif
void create_msgq_leak_check() {
        pthread_attr_t attr;
        pthread_t ptd;
        pthread_attr_init(&attr);
        size_t stack_size = 2 * 1024 * 1024;

         pthread_attr_setstacksize(&attr, stack_size);

        if(pthread_create(&ptd, &attr, &thread_start, NULL) != 0) {
                perror("pthread create");
        } 
}
