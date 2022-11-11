# Сервер для распознавания показаний счетчика

## Запуск сервера

Запустить сервер можно на Linux. Среду для запуска
нейросети можно настроить с помощью conda.

1) Создать conda среду для проекта и активировать ее:

```shell
conda env create -f environment.yml
conda activate mada-recognition
```

2) Установить дополнительные пакеты pip (не получилось устанавливать
   эти пакеты через environment.yml, поэтому они ставятся отдельно):

```shell
pip install -r requirements.txt
pip install torch==1.5.0+cu101 torchvision==0.6.0+cu101 -f https://download.pytorch.org/whl/torch_stable.html
pip install detectron2==0.2.1+cpu -f https://dl.fbaipublicfiles.com/detectron2/wheels/cpu/torch1.5/index.html
```

3) Скачать модели:

Нужно скачать архив с моделями [отсюда](https://drive.google.com/uc?export=download&id=1L-SHrvLFtp_3N0oUwCYJIC3LQtbpAxWs):

```shell
unzip models.zip
rm models.zip
```

4) Запустить сервер для распознавания показаний:

```shell
uvicorn main:app --host 127.0.0.1 --port 8000
```

После этого будет доступен обработчик, отвечающий за распознавание счетчиков для воды. Пример запроса:

```shell
curl --location --request POST '<server_ip>/recognize_meters_data' --form 'file=@"<path_to_file>"'
```

Сервер вернет число - показания счетчика. Например: `91.846`.

## Известные баги и ограничения

- Модель обучалась на датасете счетчиков для воды от [Толоки](https://toloka.ai/ru/datasets/),
  поэтому она способна распознавать только счетчики для воды.

- Модель создана по данному [руководсву](https://www.kaggle.com/code/merrickolivier/water-meters/notebook).
  Она распознает показания на фотографиях счетчиков с точностью выше 95% в около 80%
  случаев. Распознанаванию может мешать плохое качество фотографии, загрязненность
  счетчика, а также ситуации, когда последние цифры счетчика видны не полностью
