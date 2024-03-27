
ß 
V
EventListTestcom.example.collatzcheckineventListView2âÀ≠Ø¿ı“°:åÀ≠Ø¿‹¿ãŒ
‘java.lang.NullPointerException: Provided document path must not be null.
at com.google.firebase.firestore.util.Preconditions.checkNotNull(Preconditions.java:148)
at com.google.firebase.firestore.CollectionReference.document(CollectionReference.java:103)
at com.example.collatzcheckin.attendee.AttendeeDB.findUser(AttendeeDB.java:47)
at com.example.collatzcheckin.MainActivity.onCreate(MainActivity.java:61)
at android.app.Activity.performCreate(Activity.java:8595)
at android.app.Activity.performCreate(Activity.java:8573)
at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1456)
at androidx.test.runner.MonitoringInstrumentation.callActivityOnCreate(MonitoringInstrumentation.java:779)
at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:3764)
at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:3922)
at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:103)
at android.app.servertransaction.TransactionExecutor.executeCallbacks(TransactionExecutor.java:139)
at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:96)
at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2443)
at android.os.Handler.dispatchMessage(Handler.java:106)
at android.os.Looper.loopOnce(Looper.java:205)
at android.os.Looper.loop(Looper.java:294)
at android.app.ActivityThread.main(ActivityThread.java:8177)
at java.lang.reflect.Method.invoke(Native Method)
at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:552)
at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:971)java.lang.NullPointerException‘java.lang.NullPointerException: Provided document path must not be null.
at com.google.firebase.firestore.util.Preconditions.checkNotNull(Preconditions.java:148)
at com.google.firebase.firestore.CollectionReference.document(CollectionReference.java:103)
at com.example.collatzcheckin.attendee.AttendeeDB.findUser(AttendeeDB.java:47)
at com.example.collatzcheckin.MainActivity.onCreate(MainActivity.java:61)
at android.app.Activity.performCreate(Activity.java:8595)
at android.app.Activity.performCreate(Activity.java:8573)
at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1456)
at androidx.test.runner.MonitoringInstrumentation.callActivityOnCreate(MonitoringInstrumentation.java:779)
at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:3764)
at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:3922)
at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:103)
at android.app.servertransaction.TransactionExecutor.executeCallbacks(TransactionExecutor.java:139)
at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:96)
at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2443)
at android.os.Handler.dispatchMessage(Handler.java:106)
at android.os.Looper.loopOnce(Looper.java:205)
at android.os.Looper.loop(Looper.java:294)
at android.app.ActivityThread.main(ActivityThread.java:8177)
at java.lang.reflect.Method.invoke(Native Method)
at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:552)
at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:971)"ﬁ

logcatandroid»
≈C:\Users\nehal\AndroidStudioProjects\Collatz301\app\build\outputs\androidTest-results\connected\debug\Medium_Phone_API_34(AVD) - 14\logcat-com.example.collatzcheckin.EventListTest-eventListView.txt"∞

device-infoandroidï
íC:\Users\nehal\AndroidStudioProjects\Collatz301\app\build\outputs\androidTest-results\connected\debug\Medium_Phone_API_34(AVD) - 14\device-info.pb"±

device-info.meminfoandroidé
ãC:\Users\nehal\AndroidStudioProjects\Collatz301\app\build\outputs\androidTest-results\connected\debug\Medium_Phone_API_34(AVD) - 14\meminfo"±

device-info.cpuinfoandroidé
ãC:\Users\nehal\AndroidStudioProjects\Collatz301\app\build\outputs\androidTest-results\connected\debug\Medium_Phone_API_34(AVD) - 14\cpuinfo*ï
c
test-results.logOcom.google.testing.platform.runtime.android.driver.AndroidInstrumentationDriverü
úC:\Users\nehal\AndroidStudioProjects\Collatz301\app\build\outputs\androidTest-results\connected\debug\Medium_Phone_API_34(AVD) - 14\testlog\test-results.log 2
text/plain2¿
QOcom.google.testing.platform.runtime.android.driver.AndroidInstrumentationDriver"INSTRUMENTATION_FAILED*OTest run failed to complete. Instrumentation run failed due to Process crashed.2á*ÇLogcat of last crash: 
Process: com.example.collatzcheckin, PID: 7759
java.lang.RuntimeException: Unable to start activity ComponentInfo{com.example.collatzcheckin/com.example.collatzcheckin.MainActivity}: java.lang.NullPointerException: Provided document path must not be null.
	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:3782)
	at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:3922)
	at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:103)
	at android.app.servertransaction.TransactionExecutor.executeCallbacks(TransactionExecutor.java:139)
	at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:96)
	at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2443)
	at android.os.Handler.dispatchMessage(Handler.java:106)
	at android.os.Looper.loopOnce(Looper.java:205)
	at android.os.Looper.loop(Looper.java:294)
	at android.app.ActivityThread.main(ActivityThread.java:8177)
	at java.lang.reflect.Method.invoke(Native Method)
	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:552)
	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:971)
Caused by: java.lang.NullPointerException: Provided document path must not be null.
	at com.google.firebase.firestore.util.Preconditions.checkNotNull(Preconditions.java:148)
	at com.google.firebase.firestore.CollectionReference.document(CollectionReference.java:103)
	at com.example.collatzcheckin.attendee.AttendeeDB.findUser(AttendeeDB.java:47)
	at com.example.collatzcheckin.MainActivity.onCreate(MainActivity.java:61)
	at android.app.Activity.performCreate(Activity.java:8595)
	at android.app.Activity.performCreate(Activity.java:8573)
	at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1456)
	at androidx.test.runner.MonitoringInstrumentation.callActivityOnCreate(MonitoringInstrumentation.java:779)
	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:3764)
	... 12 more
