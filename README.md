# SmartHome for AndroidThings

### 概要
学習済みの学習リモコンを AndroidThings で制御して各家電を操作するものです。学習リモコン基盤でリモコンの赤外線を学習しておき、RaspberryPi の GPIO を制御することで家電を ON/OFF します。GPIO の制御のトリガーとして Firebase Realtime Database を使用しています。

### 使用機器
* RaspberryPi 3 Model B
* [ADRSIR　ラズベリー・パイ専用 学習リモコン基板](http://bit-trade-one.co.jp/product/module/adrsir/)

### OS
AndroidThings 1.0

### 開発環境
Android Studio 3.1

### ライブラリ
* [Firebase Realtime Database for Android](https://firebase.google.com/docs/database/android/start/?authuser=0)

### 注意事項
* AndroidThings は RaspberryPi 3 Model B のみをサポートしています（2018/06現在）。RaspberryPi 3 Model B+ や RaspberryPi 2 Model B などでは動作しない場合があります。
* ADRSIR　ラズベリー・パイ専用 学習リモコン基板は安定した動作のために 3A 以上の電源接続を推奨しているようですが、RaspberryPi 3 Model B 用の 2.5A の電源で動作確認しました。
