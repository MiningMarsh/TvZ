(ns twitter-zombies.graph)

(defn update-link [word [count worddict]]
  [((fnil inc 0) count)
   (update-in worddict [word] (fnil inc 0))])

(defn select-random
  "Select a random word from a link returned from a graph"
  [[count worddict]]
  (let [wordlist (seq worddict)
        [k v] (first wordlist)
        amount (inc (rand-int count))]
    (loop [amount amount
           [k v] [k v]
           wordlist (rest wordlist)]
      (if (<= (- amount v) 0)
        k
        (recur (- amount v) (first wordlist) (rest wordlist))))))

(defn add-word-link
  "Add a word link to the graph"
  [graph from-word to-word]
  (assoc graph from-word (update-link to-word (graph from-word))))


(defn add-sentence
  "Add a sentence to the graph"
  [graph sentence]
  (let [words (clojure.string/split sentence #"\s+")]
    (loop [from-words words
           to-words (rest words)
           graph graph]
      (if (empty? to-words)
        graph
        (recur (rest from-words)
               (rest to-words)
               (add-word-link graph (first from-words) (first to-words)))))))

(defn format-text
  "Gets the text into a more sane format"
  [text]
  (reduce (fn [str [f t]] (clojure.string/replace str f t))
          text
          (list [" " " "]
                ["\227" " "])))

(defn add-text
  "Adds text to graph and creates graph if none is passed in"
  ([text]
     (add-text {} text))
  ([graph text]
     (let [sentences (map #(str "> " % " .")
                          (clojure.string/split (format-text text)
                                                #"[\.\?\"\!]"))]
       (reduce #(add-sentence %1 %2) graph sentences))))

(defn replace-last-two-char
  "Pretty simple function to replace the last two characters of a string
with another"
  [from-str to-str]
  (str (subs from-str 0 (- (count from-str) 2)) to-str))

(defn random-walk
  "Return a single random sentence from the graph"
  [graph]
  (loop [last-word ">"
         sentence ""]
    (if (= last-word ".")
      (subs (replace-last-two-char sentence ".") 1)
      (let [next-word (select-random (graph last-word))]
        (recur next-word (str sentence " " next-word))))))
