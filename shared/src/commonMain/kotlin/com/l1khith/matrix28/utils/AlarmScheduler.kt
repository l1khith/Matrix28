package com.l1khith.matrix28.utils

import com.l1khith.matrix28.data.AppTask

expect fun scheduleTaskAlarm(task: AppTask)
expect fun cancelTaskAlarm(task: AppTask)
expect fun scheduleMidnightRollover()
