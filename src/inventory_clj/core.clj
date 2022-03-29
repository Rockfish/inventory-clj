(ns inventory-clj.core
  (:gen-class)
  (:require [clojure.java.io])
  (:require [clojure.string])
  (:require [cheshire.core :refer :all]))

(defn one-line [] (first (clojure.string/split-lines (slurp "/Users/jdhatch/Dev_Clojure/inventory-clj/vmi.json"))))

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


(defn get-first-record [filepath]
  (cheshire.core/parse-string
   (first (clojure.string/split-lines (slurp filepath))) true))

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


(defn -main
  "Parse json records"
  [& args]
  (println "Hello, World!")
  ;; (process-file-by-lines (str records-path (records 1)) instance-ocid))
  (get-first-record (str records-path (record-files :DbContainer))))



