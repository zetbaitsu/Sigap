# Overview

An android apps to find helper nearest of you when you have a "trouble", when danger situation or emergency. 
You need to add minimal 3 another users before you can use all the feature. Apps will notify all users who have been added previously and maximal 5 other users nearest of you when you click emergency button. As reporter (who clicked the emergency button) you can search emergency contact like police phone number, hospital, ambulance, etc. As helper you can confirm to help or no, then you can chatting with another helper, so all the helpers can create strategy, communicating and coordinating how and what the best way to help the reporter.

# Preview
<img src="https://raw.githubusercontent.com/zetbaitsu/Sigap/master/preview/1.png" width="30%"></img>
<img src="https://raw.githubusercontent.com/zetbaitsu/Sigap/master/preview/2.png" width="30%"></img>
<img src="https://raw.githubusercontent.com/zetbaitsu/Sigap/master/preview/3.png" width="30%"></img>

## Data Structure
```
└─ sigap
   ├─ users
   |  └─ userId (String)
   |     ├─ imageUrl (String)
   |     ├─ email (String)
   |     ├─ phoneNumber (String)
   |     ├─ fromApps (Boolean)
   |     ├─ male (Boolean)
   |     ├─ name (String)
   |     └─ status (Enum : SIAP, MENOLONG, BAHAYA)
   ├─ userLocations
   |  └─ userId (String)
   |     ├─ g (String)
   |     └─ l
   |        ├─ 0 (Double)
   |        └─ 1 (Double)
   ├─ userTrusted
   |  └─ userId (String)
   |     └─ userTrustedId (String)
   |        └─ status (Enum : MENUNGGU, DITERIMA, DITOLAK)
   ├─ trustedOf
   |  └─ userId (String)
   |     └─ userTrustMeId (String)
   |        └─ status (Enum : MENUNGGU, DITERIMA, DITOLAK)
   ├─ cases
   |  └─ caseId (String)
   |     ├─ address (String)
   |     ├─ detail (String)
   |     ├─ date (Long)
   |     ├─ userId (String)
   |     ├─ status (Enum : BARU, BERJALAN, DITUTUP)
   |     ├─ latitude (Double)
   |     └─ longitude (Double)
   ├─ caseLocations
   |  └─ caseId (String)
   |     ├─ g (String)
   |     └─ l
   |        ├─ 0 (Double)
   |        └─ 1 (Double)
   ├─ userCases
   |  └─ userId (String)
   |     └─ caseId (String)
   ├─ helperCases
   |  └─ caseId (String)
   |     └─ userId (String)
   |        ├─ feedback (String)
   |        └─ status (Enum : MENUNGGU, MENOLONG, MENOLAK)
   ├─ userHelps
   |  └─ userId (String)
   |     └─ caseId (String)
   |        └─ status (Enum : MENUNGGU, MENOLONG, MENOLAK)
   ├─ caseMessages
   |  └─ caseId (String)
   |     └─ messageId (String)
   |        ├─ userId (String)
   |        ├─ date (Long)
   |        └─ content (String)
   ├─ escorts
   |  └─ escortId (String)
   |     ├─ destination (String)
   |     ├─ date (Long)
   |     ├─ userId (String)
   |     ├─ latitude (Double)
   |     └─ longitude (Double)
   ├─ userEscorts
   |  └─ userId (String)
   |     └─ escortId (String)
   ├─ guards
   |  └─ escortId (String)
   |     └─ userId (String)
   |        └─ status (Enum : MENUNGGU, MENGAWAL, MENOLAK)
   ├─ userGuards
   |  └─ userId (String)
   |     └─ escortId (String)
   |        └─ status (Enum : MENUNGGU, MENGAWAL, MENOLAK)
   ├─ escortMessages
   |  └─ escortId (String)
   |     └─ messageId (String)
   |        ├─ userId (String)
   |        ├─ date (Long)
   |        └─ content (String)
   ├─ importantContacts
   |  └─ contactId (String)
   |     ├─ name (String)
   |     ├─ createdAt (Long)
   |     ├─ phoneNumber (String)
   |     ├─ userId (String)
   |     └─ address (String)
   ├─ userContacts
   |  └─ userId (String)
   |     └─ contactId (String)
   ├─ bookmarkContacts
   |  └─ userId (String)
   |     └─ contactId (String)
   └─ reviews
      └─ contactId (String)
         └─ userId (String)
            ├─ date (Long)
            ├─ description (String)
            ├─ rate (Integer)
            └─ title (String)
```


License
-------
    Copyright (c) 2015 SatuSatuDua.
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
