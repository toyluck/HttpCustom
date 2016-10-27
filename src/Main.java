import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;
import org.apache.log4j.Logger;
import retrofit2.*;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import javax.lang.model.SourceVersion;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Time;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {

    // 1m
    private static final int DIVEDE_FILE_SIZE = 1024 * 1024;
    public static final int _200KB = 1024 * 200;
    private UpVideoCombinetion videoCombinetion;
    private String _md5;
    private int _count;

    public static void main(String[] args) {
        Main main = new Main();
        main.initService();
        main.tesetMd5();
//        main.divece_file(PATH);
//        main.combineVideo();
//        main.generateMd5();
//        main.getUpVideoInfo3();
        test_transformer();
    }


    public static final String PATH = "videos/video.mp4";
    private static final String TAG = "Main";

    private void tesetMd5() {

        _md5 = generateMd5();

        Logger logger = Logger.getLogger(TAG);
        Observable<CheckMd5Model> checkMd5 = videoCombinetion.checkMd5(_md5);

        checkMd5.subscribeOn(Schedulers.immediate()).doOnNext(new Action1<CheckMd5Model>() {
            @Override
            public void call(CheckMd5Model checkMd5Model) {
                logger.debug(checkMd5Model);
                System.out.println("Main.call");
                System.out.println("subon == Thread.currentThread().getName() = " + Thread.currentThread().getName());

            }
        }).unsubscribeOn(Schedulers.io()).doOnNext(new Action1<CheckMd5Model>() {
            @Override
            public void call(CheckMd5Model checkMd5Model) {

                System.out.println(" unsub == Thread.currentThread().getName() = " + Thread.currentThread().getName());

            }
        }).filter(new Func1<CheckMd5Model, Boolean>() {
            @Override
            public Boolean call(CheckMd5Model checkMd5Model) {
                System.out.println("Filter == Thread.currentThread().getName() = " + Thread.currentThread().getName());

                return checkMd5Model.ErrNum != "102";
            }
        }).observeOn(Schedulers.immediate()).subscribe(new Subscriber<CheckMd5Model>() {
            @Override
            public void onCompleted() {
                System.out.println("!!----Thread.currentThread().getName() = " + Thread.currentThread().getName());

            }

            @Override
            public void onError(Throwable e) {
                System.out.println("e.getMessage() = " + e.getMessage());
            }

            @Override
            public void onNext(CheckMd5Model checkMd5Model) {
                System.out.println("----Thread.currentThread().getName() = " + Thread.currentThread().getName());
                upFiles();
//                getUpVideoInfo();
            }
        });
    }

    private void upFiles() {
        File[] files = divece_file(PATH).listFiles();
        Arrays.sort(files);
        Executor executor = new ThreadPoolExecutor
                (5, 5, 110, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));
        Observable.from(files).subscribeOn(Schedulers.from(executor)).filter(new Func1<File, Boolean>() {
            @Override
            public Boolean call(File file) {
                return file.exists() && file.length() > 0;
            }
        }).flatMap(new Func1<File, Observable<CheckMd5Model>>() {
            @Override
            public Observable<CheckMd5Model> call(File file) {
                return upSingleFile(file);
            }
        }).doOnNext(new Action1<CheckMd5Model>() {
            @Override
            public void call(CheckMd5Model checkMd5Model) {

            }
        }).toList().
                //todo 这个retry 是 全部的 还是 单个失败的？
                        retry(5).subscribe(new Subscriber<List<CheckMd5Model>>() {
            @Override
            public void onCompleted() {
                System.out.println("Main.onCompleted");
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("throwable.getMessage() = " + throwable.getMessage());
            }

            @Override
            public void onNext(List<CheckMd5Model> checkMd5Models) {
                System.out.println("Main.onNext%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                System.out.println("checkMd5Models = [" + checkMd5Models + "]");
                //todo delete tempfile dir
//                getUpVideoInfo();
//                getUpVideoInfo2();
                getUpVideoInfo3();
            }


        });
        List<String> objects = Collections.emptyList();
        while (true) ;
    }

    private void getUpVideoInfo3() {
        Call<UploadInfoModel> stringCall = videoCombinetion.getInfoModel3(new File(PATH).getName(), _md5);
        try {
            Response<UploadInfoModel> execute = stringCall.execute();
            System.out.println("" + execute.body());
            System.out.println("Main.getUpVideoInfo2");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("e ");
        }
    }

    private void getUpVideoInfo2() {
        Call<String> stringCall = videoCombinetion.getInfoModel2(new File(PATH).getName(), _md5);
        try {
            Response<String> execute = stringCall.execute();
            System.out.println("" + execute.body());
            System.out.println("Main.getUpVideoInfo2");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("e ");

        }
    }

    private static void test_transformer() {
        Observable.just("src").map((Func1<String, Observable<File>>) s -> {
            File file = new File(s);
            System.out.println("first  " + Thread.currentThread().getName());
            return Observable.from(file.listFiles());
        }).compose(applySchedule()).map(new Func1<Observable<File>, Observable<File>>() {
            @Override
            public Observable<File> call(Observable<File> fileObservable) {
                System.out.println("fileObservable = " + fileObservable);
                return fileObservable;
            }
        }).compose(applySchedule2()).doOnNext(new Action1<Observable<File>>() {
            @Override
            public void call(Observable<File> fileObservable) {
                System.out.println("doonnext -  Thread.currentThread().getName() = " + Thread.currentThread().getName());

            }
        }).subscribe(new Action1<Observable<File>>() {
            @Override
            public void call(Observable<File> fileObservable) {
                System.out.println("Thread.currentThread().getName() ----= " + Thread.currentThread().getName());
                System.out.println("fileObservable = " + fileObservable);
            }
        });
    }

    private static final Observable.Transformer _schedulersTransformer = new Observable.Transformer() {
        @Override
        public Object call(Object o) {
            return ((Observable) o).subscribeOn(Schedulers.immediate()).observeOn(Schedulers.computation());
        }
    };

    private static final Observable.Transformer _schedulersTransformer2 = new Observable.Transformer() {
        @Override
        public Object call(Object o) {
            return ((Observable) o).subscribeOn(Schedulers.io()).observeOn(Schedulers.computation());
        }
    };

    public static final <T> Observable.Transformer<T, T> applySchedule() {
        return (Observable.Transformer<T, T>) _schedulersTransformer;
    }


    public static final <T> Observable.Transformer<T, T> applySchedule2() {
        return (Observable.Transformer<T, T>) _schedulersTransformer2;
    }

    private void getUpVideoInfo() {
        System.out.println("DIVEDE_FILE_SIZE = " + DIVEDE_FILE_SIZE);
        generateMd5();
        Observable<UploadInfoModel> infoModel = videoCombinetion.getInfoModel(new File(PATH).getName(), _md5);

        infoModel.subscribeOn(Schedulers.immediate()).doOnSubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println("getUpVideoInfo onsubscribe ------------ ");
            }
        })
                .subscribe(new Subscriber<UploadInfoModel>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("Main.onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("e = [" + e + "]");
                    }

                    @Override
                    public void onNext(UploadInfoModel uploadInfoModel) {
                        System.out.println("uploadInfoModel = [" + uploadInfoModel + "]");
                    }
                });
    }

    /**
     * 上传单个文件
     *
     * @param file
     * @return
     */
    private Observable<CheckMd5Model> upSingleFile(File file) {

        long l = System.currentTimeMillis();

        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        Observable<CheckMd5Model> 上传成功 = videoCombinetion.upVideo(file.getName(), _count, _md5, multipartBody).doOnNext(new Action1<CheckMd5Model>() {
            @Override
            public void call(CheckMd5Model checkMd5Model) {
                System.out.println("======================\" =======================");
                System.out.println("after == Thread.currentThread().getName() = " + Thread.currentThread().getName());
                System.out.println("System.currentTimeMillis()-l = " + (System.currentTimeMillis() - l));
                System.out.println("======================\" =======================");

                if (checkMd5Model.ErrMsg.equals("上传成功")) {
                    file.delete();
                }
            }
        });
        return Observable.just(file).compose(applySchedule()).flatMap(new Func1<File, Observable<CheckMd5Model>>() {
            @Override
            public Observable<CheckMd5Model> call(File file) {
                System.out.println("-----------------------------------------------");

                System.out.println("Filter == Thread.currentThread().getName() = " + Thread.currentThread().getName());
                System.out.println("-----------------------------------------------");

                return 上传成功;
            }
        });

    }

    private void combineVideo() {
        BufferedOutputStream bos = null;
        BufferedSource bufferedSource = null;
        try {
            Source source;

            String temp_video = "temp_video.mp4";
            File temp_video_file = new File(temp_video);
            temp_video_file.delete();
            temp_video_file.createNewFile();

            FileInputStream fis;
            String name = "videos/tempvideos";

            File file = new File(name);
            File[] files = file.listFiles();

            byte[] buf;

            FileOutputStream fos = new FileOutputStream(temp_video_file, true);
            bos = new BufferedOutputStream(fos);
            Arrays.sort(files);

            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                if (f.isDirectory()) continue;
                source = Okio.source(f);
                bufferedSource = Okio.buffer(source);
                byte[] bytes = bufferedSource.readByteArray();
                bos.write(bytes);
                bos.flush();

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedSource.close();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String generateMd5() {

        File file = new File(PATH);
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "r");
            raf.seek(0);
            byte[] total_bytes = new byte[_200KB * 2];
            byte[] bytes = new byte[_200KB];
            raf.read(bytes, 0, _200KB);
            System.arraycopy(bytes, 0, total_bytes, 0, bytes.length);
            long pos = file.length() - _200KB;
            raf.seek(pos);
            raf.read(bytes);

            System.arraycopy(bytes, 0, total_bytes, _200KB, bytes.length);
            String toHex = toHex(total_bytes);
            System.out.println("toHex = " + toHex);
            _md5 = toHex;
            return toHex;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static String toHex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);

        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.reset();
        m.update(bytes);
        byte[] digest = m.digest();
        BigInteger bigInt = new BigInteger(1, digest);
        String hashtext = bigInt.toString(16);
// Now we need to zero pad it if you actually want the full 32 chars.
        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }
        return hashtext;
    }

    private void initService() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://tqjh.zfwsc.com/UploadVideo/ddUpload/")
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(StringConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        videoCombinetion = retrofit.create(UpVideoCombinetion.class);
    }


    private File divece_file(String path) {
        File file = new File(path);


        // 生成 md5

        String parent_file_name = file.getParentFile().getName();

        File inner_parent_file = new File(parent_file_name, "temp" + parent_file_name);
        if (!inner_parent_file.exists()) {
            inner_parent_file.mkdir();
        }
        // 分块数目
        _count = (int) Math.round((float) file.length() / (float) DIVEDE_FILE_SIZE);

        RandomAccessFile randomAccessFile = null;
        FileInputStream inputStream;
        FileOutputStream fos;
        try {
            randomAccessFile = new RandomAccessFile(file, "r");

            byte[] bytes;
            for (int i = 0; i < _count; i++) {
                if (i == _count - 1) {
                    bytes = new byte[(int) (file.length() - DIVEDE_FILE_SIZE * i)];
                } else
                    bytes = new byte[DIVEDE_FILE_SIZE];
                // inputStream = new FileInputStream(new File(inner_parent_file, i + ""));
                fos = new FileOutputStream(new File(inner_parent_file, i + ""), false);
                int data_size = i * DIVEDE_FILE_SIZE;
                // 修改起点位置
                randomAccessFile.seek(data_size);
                randomAccessFile.read(bytes, 0, bytes.length);
                fos.write(bytes);
                fos.flush();
                fos.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (randomAccessFile != null)
                    randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return inner_parent_file;
    }


}
