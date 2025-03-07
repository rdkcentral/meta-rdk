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

#define QUEUE_NAME_PREFIX "/mq_leak_"

typedef struct mq_msg {
    int seq;
    char message[100];
} mq_msg;

int main() {
    mqd_t mq;
    struct mq_attr attr;
    mq_msg msg;

    // Define the message queue attributes
    attr.mq_flags = 0;
    attr.mq_maxmsg = 10;
    attr.mq_msgsize = sizeof(mq_msg);
    attr.mq_curmsgs = 0;

    int pid;
    char queue_name[50];
    printf("Enter the PID to Check for Memleak:");
    scanf ("%d", &pid);
        snprintf(queue_name, sizeof(queue_name), "%s%d", QUEUE_NAME_PREFIX, pid);
        printf("Queue name:%s\n",queue_name);

    mq = mq_open(queue_name, O_WRONLY, 0644, &attr);
    if (mq < 0) {
        perror("mq_open");
        exit(1);
    }

    msg.seq = 1;
    strncpy(msg.message, "leak_check", sizeof(msg.message));

    // Send the message to the queue
    if (mq_send(mq, (const char*)&msg, sizeof(mq_msg), 0) == -1) {
        perror("mq_send");
        mq_close(mq);
        exit(1);
    }

    // Close the message queue
    mq_close(mq);

    return 0;
}
