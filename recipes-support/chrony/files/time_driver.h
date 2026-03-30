#ifndef TIME_DRIVER_H
#define TIME_DRIVER_H

typedef struct {
    const char *name;
    int (*init)(void);
    int (*step_time)(void);
    int (*get_offset)(double *offset_sec);
    int (*add_server)(const char *address);
    void (*close)(void);
} TimeDriver;

#endif
