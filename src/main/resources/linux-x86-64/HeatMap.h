#ifndef _HEATMAP_H__
#define _HEATMAP_H__

#if (defined(WIN32) || defined(_WIN32) || defined(_WIN64))

	#ifdef HEATMAP_EXPORTS
	#define HEATMAP_API  __declspec(dllexport)
	#else
	#define HEATMAP_API  __declspec(dllimport)
	#endif

	#define CALLBACK        __stdcall
	#define CALLMETHOD     __stdcall  //__cdecl
#else    //non-windows
	#define HEATMAP_API  extern "C"
	#define CALLMETHOD 

#endif


#ifdef	__cplusplus
extern "C" {
#endif

	//Bmpλͼ��Ϣ
	typedef struct bmpImageInfo
	{
		unsigned char *pBuffer;			//BmpͼƬ����ָ��
		int nWidth;						//ͼƬ���
		int nHeight;					//ͼƬ�߶�
		int nBitCount;					//ͼƬλ��,֧��8λ��24λ��32λ
		int nDirection;                 //���ݴ洢���� 0�����ϵ��£������ң� 1�����µ��ϣ�������
	}BMPIMAGE_INFO;

	///����������Ϣ
	typedef struct heatMapInfoIn
	{
		BMPIMAGE_INFO stuGrayBmpInfo;		//8λBmp�Ҷ��ȶ�ͼ���ݣ�������ͼƬͷ�����ݴ洢������ϵ���
		BMPIMAGE_INFO stuBkBmpInfo;			//����ͼBmpλͼ���ݣ�����ͼƬͷ���洢������µ���
	}HEATMAP_IMAGE_IN;

	//���������Ϣ
	typedef struct heatMapInfoOut
	{
		unsigned char *pBuffer;				 //����Ĳ�ɫ�ȶ�ͼ���ݣ�����ͼƬͷ��,��ߡ�λ���ͱ���ͼ��ͬ
		int		nPicSize;					 //ͼƬ�ڴ��С(����ͷ) ����*��*nBitCount/8 + 54
		float  fOpacity;					 //͸����,��Χ0-1
	}HEATMAP_IMAGE_Out;

	///\brief �����ȶ�ͼ������Ϣ
	/// param [in] stuBmpInfoIn       Bmpλͼ��������
	/// param [in] stuBmpInfoOut      Bmpλͼ�������,����ͼƬͷ
	/// param [out] true or false
	HEATMAP_API bool CALLMETHOD CreateHeatMap(const HEATMAP_IMAGE_IN *stuBmpInfoIn, HEATMAP_IMAGE_Out *stuBmpInfoOut);

#ifdef __cplusplus
}
#endif


#endif  //_HEATMAP_H__