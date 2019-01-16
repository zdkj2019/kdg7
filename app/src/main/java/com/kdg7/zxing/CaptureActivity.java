package com.kdg7.zxing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import com.kdg7.R;
import com.kdg7.zxing.ZXingView;
import com.kdg7.zxing.core.Delegate;

public class CaptureActivity extends Activity implements Delegate{
    private static final String TAG = CaptureActivity.class.getSimpleName();
    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 666;
    private ZXingView mZXingView;
    

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zxingview);

        mZXingView = (ZXingView) findViewById(R.id.viewfinder_view);
        
        mZXingView.setDelegate(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mZXingView.startCamera(); // �򿪺�������ͷ��ʼԤ�������ǲ�δ��ʼʶ��
//        mZXingView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT); // ��ǰ������ͷ��ʼԤ�������ǲ�δ��ʼʶ��
//        mZXingView.changeToScanBarcodeStyle();
        mZXingView.startSpotAndShowRect(); // ��ʾɨ��򣬲���ʼʶ��
        
        mZXingView.getScanBoxView().setOnlyDecodeScanBoxArea(true);
    }

    @Override
    protected void onStop() {
        mZXingView.stopCamera(); // �ر�����ͷԤ������������ɨ���
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mZXingView.onDestroy(); // ���ٶ�ά��ɨ��ؼ�
        super.onDestroy();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Log.i(TAG, "result:" + result);
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		vibrator.vibrate(200L);
        Intent intent = getIntent();
        intent.putExtra("result", result);
        setResult(2, intent);
		finish();
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        // ������ͨ���޸���ʾ�İ���չʾ�����Ƿ������״̬�����뷽Ҳ���Ը��� isDark ��ֵ��ʵ����������Ч��
        String tipText = mZXingView.getScanBoxView().getTipText();
        String ambientBrightnessTip = "\n������������������";
        if (isDark) {
            if (!tipText.contains(ambientBrightnessTip)) {
                mZXingView.getScanBoxView().setTipText(tipText + ambientBrightnessTip);
            }
        } else {
            if (tipText.contains(ambientBrightnessTip)) {
                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip));
                mZXingView.getScanBoxView().setTipText(tipText);
            }
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "���������");
    }

//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.start_preview:
//                mZXingView.startCamera(); // �򿪺�������ͷ��ʼԤ�������ǲ�δ��ʼʶ��
//                break;
//            case R.id.stop_preview:
//                mZXingView.stopCamera(); // �ر�����ͷԤ������������ɨ���
//                break;
//            case R.id.start_spot:
//                mZXingView.startSpot(); // ��ʼʶ��
//                break;
//            case R.id.stop_spot:
//                mZXingView.stopSpot(); // ֹͣʶ��
//                break;
//            case R.id.start_spot_showrect:
//                mZXingView.startSpotAndShowRect(); // ��ʾɨ��򣬲��ҿ�ʼʶ��
//                break;
//            case R.id.stop_spot_hiddenrect:
//                mZXingView.stopSpotAndHiddenRect(); // ֹͣʶ�𣬲�������ɨ���
//                break;
//            case R.id.show_scan_rect:
//                mZXingView.showScanRect(); // ��ʾɨ���
//                break;
//            case R.id.hidden_scan_rect:
//                mZXingView.hiddenScanRect(); // ����ɨ���
//                break;
//            case R.id.decode_scan_box_area:
//                mZXingView.getScanBoxView().setOnlyDecodeScanBoxArea(true); // ��ʶ��ɨ����е���
//                break;
//            case R.id.decode_full_screen_area:
//                mZXingView.getScanBoxView().setOnlyDecodeScanBoxArea(false); // ʶ��������Ļ�е���
//                break;
//            case R.id.open_flashlight:
//                mZXingView.openFlashlight(); // �������
//                break;
//            case R.id.close_flashlight:
//                mZXingView.closeFlashlight(); // �ر������
//                break;
//            case R.id.scan_one_dimension:
//                mZXingView.changeToScanBarcodeStyle(); // �л���ɨ��������ʽ
//                mZXingView.setType(BarcodeType.ONE_DIMENSION, null); // ֻʶ��һά����
//                mZXingView.startSpotAndShowRect(); // ��ʾɨ��򣬲���ʼʶ��
//                break;
//            case R.id.scan_two_dimension:
//                mZXingView.changeToScanQRCodeStyle(); // �л���ɨ���ά����ʽ
//                mZXingView.setType(BarcodeType.TWO_DIMENSION, null); // ֻʶ���ά����
//                mZXingView.startSpotAndShowRect(); // ��ʾɨ��򣬲���ʼʶ��
//                break;
//            case R.id.scan_qr_code:
//                mZXingView.changeToScanQRCodeStyle(); // �л���ɨ���ά����ʽ
//                mZXingView.setType(BarcodeType.ONLY_QR_CODE, null); // ֻʶ�� QR_CODE
//                mZXingView.startSpotAndShowRect(); // ��ʾɨ��򣬲���ʼʶ��
//                break;
//            case R.id.scan_code128:
//                mZXingView.changeToScanBarcodeStyle(); // �л���ɨ��������ʽ
//                mZXingView.setType(BarcodeType.ONLY_CODE_128, null); // ֻʶ�� CODE_128
//                mZXingView.startSpotAndShowRect(); // ��ʾɨ��򣬲���ʼʶ��
//                break;
//            case R.id.scan_ean13:
//                mZXingView.changeToScanBarcodeStyle(); // �л���ɨ��������ʽ
//                mZXingView.setType(BarcodeType.ONLY_EAN_13, null); // ֻʶ�� EAN_13
//                mZXingView.startSpotAndShowRect(); // ��ʾɨ��򣬲���ʼʶ��
//                break;
//            case R.id.scan_high_frequency:
//                mZXingView.changeToScanQRCodeStyle(); // �л���ɨ���ά����ʽ
//                mZXingView.setType(BarcodeType.HIGH_FREQUENCY, null); // ֻʶ���Ƶ�ʸ�ʽ������ QR_CODE��UPC_A��EAN_13��CODE_128
//                mZXingView.startSpotAndShowRect(); // ��ʾɨ��򣬲���ʼʶ��
//                break;
//            case R.id.scan_all:
//                mZXingView.changeToScanQRCodeStyle(); // �л���ɨ���ά����ʽ
//                mZXingView.setType(BarcodeType.ALL, null); // ʶ���������͵���
//                mZXingView.startSpotAndShowRect(); // ��ʾɨ��򣬲���ʼʶ��
//                break;
//            case R.id.scan_custom:
//                mZXingView.changeToScanQRCodeStyle(); // �л���ɨ���ά����ʽ
//
//                Map<DecodeHintType, Object> hintMap = new EnumMap<>(DecodeHintType.class);
//                List<BarcodeFormat> formatList = new ArrayList<>();
//                formatList.add(BarcodeFormat.QR_CODE);
//                formatList.add(BarcodeFormat.UPC_A);
//                formatList.add(BarcodeFormat.EAN_13);
//                formatList.add(BarcodeFormat.CODE_128);
//                hintMap.put(DecodeHintType.POSSIBLE_FORMATS, formatList); // ���ܵı����ʽ
//                hintMap.put(DecodeHintType.TRY_HARDER, Boolean.TRUE); // �������ʱ������Ѱ��ͼ�ϵı��룬�Ż�׼ȷ�ԣ������Ż��ٶ�
//                hintMap.put(DecodeHintType.CHARACTER_SET, "utf-8"); // �����ַ���
//                mZXingView.setType(BarcodeType.CUSTOM, hintMap); // �Զ���ʶ�������
//
//                mZXingView.startSpotAndShowRect(); // ��ʾɨ��򣬲���ʼʶ��
//                break;
//            case R.id.choose_qrcde_from_gallery:
//                /*
//                �����ѡȡ��ά��ͼƬ������Ϊ�˷�����ʾ��ʹ�õ���
//                https://github.com/bingoogolapple/BGAPhotoPicker-Android
//                ���������ͼ����ѡ���ά��ͼƬ������ⲻ�Ǳ���ģ���Ҳ����ͨ���Լ��ķ�ʽ��ͼ����ѡ��ͼƬ
//                 */
//                Intent photoPickerIntent = new BGAPhotoPickerActivity.IntentBuilder(this)
//                        .cameraFileDir(null)
//                        .maxChooseCount(1)
//                        .selectedPhotos(null)
//                        .pauseOnScroll(false)
//                        .build();
//                startActivityForResult(photoPickerIntent, REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY);
//                break;
//        }
//    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mZXingView.startSpotAndShowRect(); // ��ʾɨ��򣬲���ʼʶ��

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY) {
//            final String picturePath = BGAPhotoPickerActivity.getSelectedPhotos(data).get(0);
//            // �������õ� QRCodeView ʱ��ֱ�ӵ� QRCodeView �ķ�������ͨ�õĻص�
//            mZXingView.decodeQRCode(picturePath);

            /*
            û���õ� QRCodeView ʱ���Ե��� QRCodeDecoder �� syncDecodeQRCode ����

            ����Ϊ��͵������û�д������� AsyncTask �ڲ��ർ�� Activity й©������
            �뿪����ʹ��ʱ���д��������ڲ��ർ��Activity�ڴ�й©�����⣬����ʽ�ɲο� https://github
            .com/GeniusVJR/LearningNotes/blob/master/Part1/Android/Android%E5%86%85%E5%AD%98%E6%B3%84%E6%BC%8F%E6%80%BB%E7%BB%93.md
             */
//            new AsyncTask<Void, Void, String>() {
//                @Override
//                protected String doInBackground(Void... params) {
//                    return QRCodeDecoder.syncDecodeQRCode(picturePath);
//                }
//
//                @Override
//                protected void onPostExecute(String result) {
//                    if (TextUtils.isEmpty(result)) {
//                        Toast.makeText(TestScanActivity.this, "δ���ֶ�ά��", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(TestScanActivity.this, result, Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }.execute();
        }
    }
}