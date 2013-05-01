bulletproof
===========

A lock screen for Google Glass. 

WARNING this could lock you out of your device if you forget or can't reproduce your code correctly, so use with caution. Single taps and long presses are the easiest to reliable hit without any training.

- Edit MainActivity.CODE to change your passcode. 
- Turn on ADB by going into settings, scrolling to device info, tapping once, then scrolling to enable debug mode
- Enable on On-Head detection in the settings for this to work
- You can still take pictures while it's locked via the camera button. I think this is great. Others may not, I'll look into if I can stop that at some point.
- FLING_LEFT is a quick swipe gesture from the back of your head towards the front on the touchpad, FLING_RIGHT is the opposite. Everything else should be self explanatory. Got it stuck on? Just do an ADB uninstall.


glass@mikedg.com

Copyright 2013 Michael DiGiovanni

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
