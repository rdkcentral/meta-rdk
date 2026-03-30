#ifndef TIME_DRIVER_H
#define TIME_DRIVER_H

#include "addressing.h"

typedef struct {
    const char *name;
    int (*init)(void);
    int (*step_time)(void);
    int (*get_offset)(double *offset_sec);
    int (*add_server)(const char *address);
    int (*send_burst)(const IPAddr *addr, const IPAddr *mask, int n_good_samples, int n_total_samples);
    void (*close)(void);
} TimeDriver;

#endif
