(defproject clj-twiml "0.1.4"
  :description "Clojure Twilio TwiML DSL (https://www.twilio.com/docs/api/twiml)."
  :url "https://github.com/shaunparker/clj-twiml"
  :license {:name "The MIT License (MIT)"
            :url "https://github.com/shaunparker/clj-twiml#the-mit-license"
            :distribution :repo}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/data.xml "0.0.7"]]
  :profiles {:1.4 {:dependencies [[org.clojure/clojure "1.4.0"]]}
             :dev {}}
  :test-paths ["test/"]
  :jar-name "clj-twiml.jar"
  :aliases {"all" ["with-profile" "dev:dev,1.4"]})
