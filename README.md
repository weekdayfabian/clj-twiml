# clj-twiml

Clojure Twilio TwiML DSL (https://www.twilio.com/docs/api/twiml). Based on [Seth Buntin's clj-twilio library](https://github.com/sethtrain/clj-twilio).

Add this to your project.clj :dependencies list:

    [clj-twiml "0.1.4"]

## Example Usage

	user=> (require '[twiml.core :as t])
	nil
	
	user=> (t/response (t/say "Hello, World!"))
	#:clojure.data.xml.Element{:tag :Response, :attrs {}, :content (#:clojure.data.xml.Element{:tag :Say, :attrs {}, :content ("Hello, World!")})}
	
	user=> (t/response-str (t/say "Hello, World!"))
	"<?xml version=\"1.0\" encoding=\"UTF-8\"?><Response><Say>Hello, World!</Say></Response>"
	
	user=> (t/response-str (t/say "Hello, World!") (t/pause {:length 3}) (t/say "World, are you there?"))
	"<?xml version=\"1.0\" encoding=\"UTF-8\"?><Response><Say>Hello, World!</Say><Pause length=\"3\"></Pause><Say>World, are you there?</Say></Response>"
	
	user=> (t/response-str
	         (t/gather {:action "/process_gather.php" :method "GET"}
	           (t/say "Please enter your account number, followed by the pound sign"))
	         (t/say "We didn't receive any input. Goodbye!"))
	"<?xml version=\"1.0\" encoding=\"UTF-8\"?><Response><Gather action=\"/process_gather.php\" method=\"GET\"><Say>Please enter your account number, followed by the pound sign</Say></Gather><Say>We didn't receive any input. Goodbye!</Say></Response>"
	
	user=> (doc t/say)
	-------------------------
	twiml.core/say
	([content__321__auto__] [attrs__322__auto__ content__321__auto__])
	  The <Say> verb converts text to speech that is read back to the caller. <Say>
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
	  language: see https://www.twilio.com/docs/api/twiml/say#attributes-language

## The MIT License

Copyright (c) 2010 Seth Buntin

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
