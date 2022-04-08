(ns inventory-clj.core
  (:gen-class)
  (:require [clojure.java.io])
  (:require [clojure.string])
  (:require [cheshire.core :refer :all])
  (:require [clojure.spec.alpha :as s]))


(def records-path "/Users/jdhatch/Records/Source/")

(def record-files {:VmiHypervisorData "2022-03-01T00.10.20Z_vmi_VmiHypervisorData_000006_eu-zurich-1.json"
                   :VmiInstanceData "2022-03-01T00.10.20Z_vmi_VmiInstanceData_000007_eu-zurich-1.json"
                   :DbContainer "2022-03-01T00.48.40Z_dbcontainer_DbContainer_000001_eu-zurich-1.json"
                   :ComputeAdminHost "2022-03-01T00.48.41Z_compute_ComputeAdminHost_000001_eu-zurich-1.json"
                   :LbaasHost "2022-03-01T00.50.02Z_lbaas_LbaasHost_000002_eu-zurich-1.json"
                   :LbaasRecord "2022-03-01T00.50.02Z_lbaas_LbaasRecord_000001_eu-zurich-1.json"
                   :CardManagementCardLiquidio "2022-03-01T00.53.12Z_cardmanagement_CardManagementCardLiquidio_000001_eu-zurich-1.json"
                   :HopsCavium "2022-03-01T00.59.12Z_hops_HopsCavium_000002_eu-zurich-1.json"
                   :HopsHost "2022-03-01T00.59.12Z_hops_HopsHost_000001_eu-zurich-1.json"
                   :VmiAttachmentData "2022-03-01T01.10.44Z_vmi_VmiAttachmentData_000005_eu-zurich-1.json"
                   :VmiVnicAttachmentData "2022-03-01T01.10.44Z_vmi_VmiVnicAttachmentData_000008_eu-zurich-1.json"})

(defn one-line [] (first (clojure.string/split-lines (slurp (str records-path (record-files :VmiInstanceData))))))

(defn process-file-by-lines
  "Process file reading it line-by-line"
  ([file]
   (process-file-by-lines file identity))
  ([file process-fn]
   (process-file-by-lines file process-fn println))
  ([file process-fn output-fn]
   (with-open [rdr (clojure.java.io/reader file)]
     (doseq [line (line-seq rdr)]
       (output-fn
        (process-fn line))))))

(defn get-instance [line] ((cheshire.core/parse-string line true) :instance_ocid))

(defn get-instances [filename] (process-file-by-lines filename get-instance))

(def vmi-file (str records-path (record-files :VmiInstanceData)))

(defn get-instance-ocids [filename]
  (with-open [rdr (clojure.java.io/reader filename)]
    (mapv get-instance (line-seq rdr))))

(defn last-part
  "Returns last num chars of line"
  [num line]
  (.substring line (- (count line) num)))

(defn mod-hash
  "Returns the mod div of the last num chars of the line"
  [div num line] (mod (reduce + (map int (char-array (last-part num line)))) div))

(def not-nil? (complement nil?))

(def instances (get-instance-ocids vmi-file))

(defn print-instances [] (doseq [line (filter not-nil? instances)] (println (mod-hash 10 5 line))))

(defn mod-instances
  "Returns the mod-hash div of the last num chars of each instance ocid"
  [div num] (map (partial mod-hash div num) (filter not-nil? instances)))


(defn get-first-record [filepath]
  (cheshire.core/parse-string
   (first (clojure.string/split-lines (slurp filepath))) true))

(s/def :VmiHypervisorData/compartment_id string?)

(def docker-list (slurp "/Users/jdhatch/Dev/Temp/docker-list.json"))

(def get-docker-list (cheshire.core/parse-string docker-list true))
(def get-docker-list-json (cheshire.core/parse-string docker-list))

(def get-unique-docker-actions (into #{} (map :action get-docker-list)))

(def vmi-sample {:ad "eu-zurich-1-ad-1",
                 :compartment_id "ocid1.tenancy.oc1..aaaaaaaa27yjl4rkq2k36aq6wrihit7hwh7edelz2yz6vlrvergthidqdg3a",
                 :display_name "",
                 :fabric_layer "overlay",
                 :host_serial "1924XD600C",
                 :instance_ocid "ocid1.instance.oc1.eu-zurich-1.ab5heljr22ho3qoduawklsmwywq3ddnpw4s2ihckrvdcyapba6lvziw7xfyq",
                 :region "eu-zurich-1",
                 :shape "VM.Standard2.16",
                 :state "Running",
                 :tenant_id "ocid1.tenancy.oc1..aaaaaaaa27yjl4rkq2k36aq6wrihit7hwh7edelz2yz6vlrvergthidqdg3a",
                 :time_created "2019-08-16 09:50:27",
                 :time_terminated ""})

(defn -main
  "Parse json records"
  [& args]
  (println "Hello, World!")
  ;; (process-file-by-lines (str records-path (records 1)) instance-ocid))
  (get-first-record (str records-path (record-files :DbContainer))))



