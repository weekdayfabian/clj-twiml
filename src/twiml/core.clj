(ns twiml.core
  "DSL for Twilio's TwiML."
  (:require [clojure.data.xml :as xml]
            [clojure.string :as string]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; DSL Macros

(defmacro single-verb [name doc-str]
  (let [tag (keyword (string/capitalize (str name)))]
    `(defn ~name ~doc-str [] [~tag])))

(defmacro simple-verb [name doc-str]
  (let [tag (keyword (string/capitalize (str name)))]
    `(defn ~name
       ~doc-str
       ([]
         (~name {}))
       ([attrs#]
         [~tag attrs#]))))

(defmacro content-verb [name doc-str]
  (let [tag (keyword (string/capitalize (str name)))]
    `(defn ~name
       ~doc-str
       ([content#]
         (~name {} content#))
       ([attrs# content#]
         [~tag attrs# content#]))))

(defmacro nestable-verb [name doc-str]
  (let [tag (keyword (string/capitalize (str name)))]
    `(defn ~name
       ~doc-str
       ([]
         [~tag {} nil])
       ([nested#]
         (~name {} nested#))
       ([attrs# & nested#]
         [~tag attrs# nested#]))))

(defmacro content-noun
  [name doc-str]
  `(content-verb ~name ~doc-str))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Public

(defmacro response
  "Parses the provided tokens into TwiML Response element.
  Uses clojure.data.xml/Element to represent the resonse element."
  [& tokens]
  `(xml/sexp-as-element [:Response ~@tokens]))

(defmacro response-str
  "Helper function which returns a string of the parsed TwiML Response."
  [& tokens]
  `(-> (response ~@tokens)
       (xml/emit-str)))

;; Primary Verbs

(content-verb say
  "The <Say> verb converts text to speech that is read back to the caller. <Say>
  is useful for development or saying dynamic text that is difficult to
  pre-record. The current verb offers different options for voices, each with
  its own supported set of languages and genders, so configure your TwiML
  depending on preferred gender and language combination.

  Supports different attributes, depending on the 'voice' value you set.

  The <Say> verb allows two separate voice engines. The first with the voices
  man and woman supports the English, Spanish, French, German, and Italian
  languages in both genders. The second, alice, speaks even more languages with
  support for several different locales in a female voice.

  voice: man, woman, alice
    (default: man (for limited languages);
              alice: (for additional languages/locales))
  loop: integer >= 0 (default: 1)
  language: see https://www.twilio.com/docs/api/twiml/say#attributes-language")

(content-verb play
  "The <Play> verb plays an audio file back to the caller. Twilio retrieves
  the file from a URL that you provide.

  Supports the following attributes that modify its behavior:

  loop: integer >= 0 (default: 1
  digits: integer >= 0 (default: none)")

(nestable-verb gather
  "The <Gather> verb collects digits that a caller enters into his or her
  telephone keypad. When the caller is done entering data, Twilio submits that
  data to the provided 'action' URL in an HTTP GET or POST request, just like
  a web browser submits data from an HTML form.

  If no input is received before timeout, <Gather> falls through to the next
  verb in the TwiML document.

  You may optionally nest <Say> and <Play> verbs within a <Gather> verb while
  waiting for input. This allows you to read menu options to the caller while
  letting her enter a menu selection at any time. After the first digit is
  received the audio will stop playing.

  Supports the following attributes that modify its behavior:

  action: relative or absolute URL (default: current document URL)
  method: GET, POST (default: POST)
  timeout: positive integer (default: 5 seconds)
  finishOnKey: any digit, #, or * (default: #)
  numDigits: integer >= 1 (default: unlimited)")

(simple-verb record
  "The <Record> verb records the caller's voice and returns to you the URL of a
  file containing the audio recording. You can optionally generate text
  transcriptions of recorded calls by setting the 'transcribe' attribute of the
  <Record> verb to 'true'.

  Supports the following attributes that modify its behavior:

  action: relative or absolute URL (default: current document URL)
  method: GET, POST (default: POST)
  timeout: positive integer (default: 5)
  finishOnKey: any digit, #, * (default: 1234567890*#)
  maxLength: integer greater than 1 (default: 3600 (1 hour))
  transcribe: true, false (default: false)
  transcribeCallback: relative or absolute URL (default: none)
  playBeep: true, false (default: true)")

(content-verb message
  "The <Message> verb sends a message to a phone number.

  Supports the following attributes that modify its behavior:

  to: phone number
    (see https://www.twilio.com/docs/api/twiml/sms/message#attributes-to)
  from: phone number
    (see https://www.twilio.com/docs/api/twiml/sms/message#attributes-from)
  action: relative or absolute URL (default: none)
  method: GET, POST (default: POST)
  statusCallback: relative or absolute URL (default: none)")

(nestable-verb dial
  "The <Dial> verb connects the current caller to another phone. If the called
  party picks up, the two parties are connected and can communicate until one
  hangs up. If the called party does not pick up, if a busy signal is received,
  or if the number doesn't exist, the dial verb will finish.

  When the dialed call ends, Twilio makes a GET or POST request to the 'action'
  URL if provided. Call flow will continue using the TwiML received in
  response to that request.

  Supports the following attributes that modify its behavior:

  action: relative or absolute URL (default: none)
  method: GET, POST (default: POST)
  timeout: positive integer (default: 30 seconds)
  hangupOnStar: true, false (default: false)
  timeLimit: positive integer (seconds) (default: 14400 seconds (4 hours))
  callerId: a valid phone number, or client identifier if you are dialing a
    <Client>. (default: Caller's callerId)
  record: true, false (default: false)")

;; Dail Nouns

(content-noun number
  "The <Dial> verb's <Number> noun specifies a phone number to dial. Using the
  noun's attributes you can specify particular behaviors that Twilio should
  apply when dialing the number.

  You can use up to ten <Number> nouns within a <Dial> verb to simultaneously
  call all of them at once. The first call to pick up is connected to the
  current call and the rest are hung up.

  Supports the following attributes that modify its behavior:

  sendDigits: any digits (default: none)
  url: any url (default: none)
  method: GET, POST (default: POST)")

(content-noun sip
  "The <Dial> verb's <Sip> noun lets you set up VoIP sessions by using SIP --
  Session Initiation Protocol. With this feature, you can send a call to any SIP
  endpoint. Set up your TwiML to use the <Sip> noun within the <Dial> verb
  whenever any of your Twilio phone numbers are called. If you are unfamiliar
  with SIP, or want more information on how Twilio works with your SIP endpoint,
  please see the SIP overview.

  The SIP session:
  The SIP INVITE message includes the API version, the AccountSid and CallSid
  for the call. Also, configure Twilio to pass custom SIP headers in the INVITE
  message. Optionally, provide a set of parameters to manage signaling transport
  and authentication.

  Once the SIP session completes, Twilio requests the <Dial> action URL, passing
  along the SIP CallID header, the response code of the invite attempt, any
  X-headers passed back on the final SIP response, as well as the standard
  Twilio <Dial> parameters.

  Currently, only one <Sip> noun may be specified per <Dial>, and the INVITE
  message may be sent to only one SIP endpoint. Also, you cannot add any other
  nouns (eg <Number>, <Client>) in the same <Dial> as the SIP. If you want to
  use another noun, set up a callback on the <Dial> to use alternate methods.

  All of the <Dial> parameters work with the <Sip> noun
  (record, timeout, hangupOnStar, etc). For SIP calls, the callerId attribute
  does not need to be a validated phone number. Enter any alphanumeric string.
  Optionally include the following chars: +-_., but no whitespace.

  Within the <Sip> noun, you must specify a URI for Twilio to connect to.
  The URI should be a valid SIP URI under 255 characters.")

(content-noun client
  "The <Dial> verb's <Client> noun specifies a client identifier to dial.

  You can use up to ten <Client> nouns within a <Dial> verb to simultaneously
  attempt a connection with many clients at once. The first client to accept the
  incoming connection is connected to the call and the other connection attempts
  are canceled. If you want to connect with multiple other clients
  simultaneously, read about the <Conference> noun.

  The client identifier currently may only contain alpha-numeric and
  underscore characters.

  Supports the following attributes that modify its behavior:

  url: any url (default: none)
  method: GET, POST (default: POST)")

(content-noun conference
  "The <Dial> verb's <Conference> noun allows you to connect to a conference
  room. Much like how the <Number> noun allows you to connect to another phone
  number, the <Conference> noun allows you to connect to a named conference room
  and talk with the other callers who have also connected to that room.

  The name of the room is up to you and is namespaced to your account. This
  means that any caller who joins 'room1234' via your account will end up in the
  same conference room, but callers connecting through different accounts would
  not. The maximum number of participants in a single Twilio conference
  room is 40.

  By default, Twilio conference rooms enable a number of useful features used
  by business conference bridges:

  Conferences do not start until at least two participants join. While waiting,
  customizable background music is played.

  When participants join and leave, notification sounds are played to inform the
  other participants. You can configure or disable each of these features based
  on your particular needs.

  Supports the following attributes that modify its behavior:

  muted: true, false (default: false)
  beep: true, false, onEnter, onExit (default: true)
  startConferenceOnEnter: true, false (default: true)
  endConferenceOnExit: true, false (default: false)
  waitUrl: TwiML url, empty string (default: default Twilio hold music)
  waitMethod: GET or POST (default: POST)
  maxParticipants: positive integer <= 40 (default: 40)")

(content-noun queue
  "The <Dial> verb's <Queue> noun specifies a queue to dial. When dialing a
  queue, the caller will be connected with the first enqueued call in the
  specified queue. If the queue is empty, Dial will wait until the next person
  joins the queue or until the timeout duration is reached. If the queue does
  not exist, Dial will post an error status to its action URL.

  Supports the following attributes that modify its behavior:

  url: relative or absolute URL (default: none)
  method: GET, POST (default: POST)")

;; Secondary Verbs

(simple-verb enqueue
  "The <Enqueue> verb enqueues the current call in a call queue.
  Enqueued calls wait in hold music until the call is dequeued by another
  caller via the <Dial> verb or transfered out of the queue via the REST API
  or the <Leave> verb.

  The <Enqueue> verb will create a queue on demand, if the queue does not
  already exist.

  Supports the following attributes that modify its behavior:

  action: relative or absolute URL (default: none)
  method: GET, POST (default: POST)
  waitUrl: relative or absolute URL (default: none)
  waitUrlMethod: GET, POST (default: POST)")

(single-verb leave
  "The <Leave> verb transfers control of a call that is in a queue so that the
  caller exits the queue and execution continues with the next verb after the
  original <Enqueue>.")

(single-verb hangup
  "The <Hangup> verb ends a call. If used as the first verb in a TwiML response
  it does not prevent Twilio from answering the call and billing your account.
  The only way to not answer a call and prevent billing is to use the <Reject>
  verb.")

(content-verb redirect
  "The <Redirect> verb transfers control of a call to the TwiML at a different
  URL. All verbs after <Redirect> are unreachable and ignored.

  Supports the following attributes that modify its behavior:

  method: GET, POST (default POST)")

(simple-verb reject
  "The <Reject> verb rejects an incoming call to your Twilio number without
  billing you. This is very useful for blocking unwanted calls.

  If the first verb in a TwiML document is <Reject>, Twilio will not pick up
  the call. The call ends with a status of 'busy' or 'no-answer', depending on
  the verb's 'reason' attribute. Any verbs after <Reject> are unreachable and
  ignored.

  Note that using <Reject> as the first verb in your response is the only way
  to prevent Twilio from answering a call. Any other response will result in an
  answered call and your account will be billed.

  Supports the following attributes that modify its behavior:

  reason: rejected or busy (default: rejected)")

(simple-verb pause
  "The <Pause> verb waits silently for a specific number of seconds.
  If <Pause> is the first verb in a TwiML document, Twilio will wait the
  specified number of seconds before picking up the call.

  Supports the following attributes that modify its behavior:

  length: positive integer (default: 1 second)")
