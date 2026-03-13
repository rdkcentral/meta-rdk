/**
 * @file libchronyctl.h
 * @brief Thread-safe shared library for chronyd interaction
 */

#ifndef LIBCHRONYCTL_H
#define LIBCHRONYCTL_H

#include <stdint.h>
#include <stddef.h>

#ifdef __cplusplus
extern "C" {
#endif

/**
 * @brief Library error codes
 */
typedef enum {
    CHRONYCTL_SUCCESS = 0,
    CHRONYCTL_ERROR_INIT = -1,
    CHRONYCTL_ERROR_NOT_INIT = -2,
    CHRONYCTL_ERROR_EXEC = -3,
    CHRONYCTL_ERROR_PARSE = -4,
    CHRONYCTL_ERROR_INVALID = -5,
    CHRONYCTL_ERROR_MUTEX = -6,
    CHRONYCTL_ERROR_NO_DATA = -7,
    CHRONYCTL_ERROR_UNAUTH = -8
} chronyctl_error;

/**
 * @brief Initialize the library
 * @return CHRONYCTL_SUCCESS on success, error code otherwise
 */
int chronyctl_init(void);

/**
 * @brief Cleanup library resources
 * @return CHRONYCTL_SUCCESS on success, error code otherwise
 */
int chronyctl_cleanup(void);

/**
 * @brief Get current time offset from chronyd
 * @param offset_sec Pointer to double to store offset in seconds
 * @return CHRONYCTL_SUCCESS on success, error code otherwise
 */
int chronyctl_get_offset(double *offset_sec);

/**
 * @brief Force chronyd to step the system clock
 * @return CHRONYCTL_SUCCESS on success, error code otherwise
 */
int chronyctl_makestep(void);

/**
 * @brief Add a new NTP server
 * @param address Hostname or IP of the server
 * @param minpoll Min poll interval (log2 seconds, e.g., 6 for 64s)
 * @param maxpoll Max poll interval (log2 seconds, e.g., 10 for 1024s)
 * @return CHRONYCTL_SUCCESS on success, error code otherwise
 */
int chronyctl_add_server(const char *address, int minpoll, int maxpoll);

/**
 * @brief Delete an NTP server
 * @param address IP address of the server to delete
 * @return CHRONYCTL_SUCCESS on success, error code otherwise
 */
int chronyctl_delete_server(const char *address);

/**
 * @brief Update poll intervals for a server
 * @param address IP address of the server
 * @param minpoll New min poll interval
 * @param maxpoll New max poll interval
 * @return CHRONYCTL_SUCCESS on success, error code otherwise
 */
int chronyctl_set_poll(const char *address, int minpoll, int maxpoll);

/**
 * @brief Get human-readable error message
 * @param err Error code
 * @return Const string description
 */
const char* chronyctl_strerror(int err);

#ifdef __cplusplus
}
#endif

#endif /* LIBCHRONYCTL_H */
