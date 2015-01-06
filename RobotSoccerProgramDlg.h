
// RobotSoccerProgramDlg.h : header file
//

#pragma once

#include "afxcolorbutton.h"
#include "propertypagecolorsetting.h"
#include "propertypagevision.h"
#include "propertypagegame.h"
#include "propertypagestrategygui.h"
#include "imagegrabber_usbcam.h"
#include "imagegrabber_file.h"
#include "timegraph.h"
#include "timecheck.h"
#include "framedc.h"

#include"FontPropertySheet.h"
#include"PropertyPageColorSetting.h"
#include"PropertyPageVision.h"
#include"PropertyPageGame.h"
#include"PropertyPageStrategyGUI.h"

#include "ObjectPositionInfo.h"
#include "ObjectVelocityInfo.h"
#include "SynchronousModule.h"
#include "afxwin.h"

#include "Ground.h"
#include "CommThread.h"
#include "dualslider.h"

// CRobotSoccerProgramDlg dialog
class CRobotSoccerProgramDlg : public CDialogEx
{
// Construction
public:
	CRobotSoccerProgramDlg(CWnd* pParent = NULL);	// standard constructor

// Dialog Data
	enum { IDD = IDD_ROBOTSOCCERPROGRAM_DIALOG };

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV support


// Implementation
protected:
	HICON m_hIcon;

	// Generated message map functions
	virtual BOOL OnInitDialog();
	afx_msg void OnSysCommand(UINT nID, LPARAM lParam);
	afx_msg void OnPaint();
	afx_msg HCURSOR OnQueryDragIcon();
	DECLARE_MESSAGE_MAP()


protected:
	int m_RobotTestBehavior;
	CCommThread m_ComPort;

	void MouseCapture( MouseCapture::Enum capture );
	bool Initialization(void);
	CFontPropertySheet m_PropertySheet;
	CPropertyPageColorSetting m_propColorSetting;
	CPropertyPageVision       m_propVision;
	CPropertyPageGame         m_propGame;
	CPropertyPageStrategyGUI  m_propStrategyGUI;

	CPropertyPageVisionCoreDiagonalPatch m_propTest;

	bool m_bMouseCapture;
	
	int m_cameraID;
	bool m_bChangeCamera;
	
	bool m_bExit;
	CTimeCheck m_timeDraw;
	CTimeCheck m_timeFrame;
	CTimeCheck m_timeProgram;
	CTimeCheck m_timeProcess;
	CTimeCheck m_timeStrategy;
	CTimeCheck m_timeVision;
	
	CFrameDC m_dcDisplay;
	CLookUpTable* m_pLookUpTable;
	CGround m_ground;

	CTimeGraph m_graphMain;
	CWinThread *m_pThreadDraw;
	CImageGrabber_USBCam m_ImageGrabber;
	//CImageGrabber_File m_ImageGrabber;

	enum{
		GRAPH_TIME_FRAME    ,
		GRAPH_TIME_PROCESS  ,
		GRAPH_TIME_DRAW     ,
		GRAPH_TIME_VISION   ,
		GRAPH_TIME_STRATEGY
	};

	void DrawGraph(void);

	CPoint m_pointMouse;
	UINT m_nCursorID;
	void SetNewCursor(unsigned int cursorID);


	CObjectPositionInfo m_ObjectInfo;
	CObjectVelocityInfo m_VelocityInfo;

	CSynchronousModule m_SynchronousModule;

	void SendVelocity(void);
	void InitializeSerialPort(int port);
	bool m_bTransmitter;

	FILE *m_fpRobotInfo;

public:
	void DrawDisplay(void);
	void Process(void);


public:
	//port connection
	void closeConnection();
	void sendStuff(int data);
	void sendStuff(std::string data);
	bool connectToHost(int  portNumber);

	bool has_only_digit;

	CString m_portNumber;
	CString m_connectionStatus;

	SOCKET s;

	afx_msg void OnBnClickedConnectToHost();
	afx_msg void OnBnClickedOpenRobotsoccerSystem();
	afx_msg void OnDestroy();
	afx_msg void OnLButtonDown(UINT nFlags, CPoint point);
	afx_msg void OnLButtonUp(UINT nFlags, CPoint point);
	afx_msg void OnRButtonDown(UINT nFlags, CPoint point);
	afx_msg void OnRButtonUp(UINT nFlags, CPoint point);
	afx_msg void OnMouseMove(UINT nFlags, CPoint point);
	afx_msg void OnBnClickedCancel();
	BOOL m_bManualRobotAll;
	BOOL m_bManualRobot1;
	BOOL m_bManualRobot2;
	BOOL m_bManualRobot3;
	afx_msg void OnBnClickedCheckRobotAll();
	afx_msg void OnBnClickedCheckRobot1();
	afx_msg void OnBnClickedCheckRobot2();
	afx_msg void OnBnClickedCheckRobot3();
	afx_msg void OnBnClickedCheckSimulation();
	BOOL m_bSimulationMode;
	CComboBox m_comboCOMPort;
	CString m_strCOMPort;
	BOOL m_bManualControl;
	afx_msg void OnBnClickedCheckTransmitterTest();
	afx_msg void OnSelchangeComboComPort();
	afx_msg void OnBnClickedSaveRobotsoccerSystem();
	CString m_strProjectName;
	afx_msg void OnBnClickedCheckRobot4();
	afx_msg void OnBnClickedCheckRobot5();
	afx_msg void OnBnClickedCheckRobot6();
	afx_msg void OnBnClickedCheckRobot7();
	afx_msg void OnBnClickedCheckRobot8();
	afx_msg void OnBnClickedCheckRobot9();
	afx_msg void OnBnClickedCheckRobot12();
	afx_msg void OnBnClickedCheckRobot13();
	BOOL m_bManualRobot4;
	BOOL m_bManualRobot5;
	BOOL m_bManualRobot6;
	BOOL m_bManualRobot7;
	BOOL m_bManualRobot8;
	BOOL m_bManualRobot9;
	BOOL m_bManualRobot10;
	BOOL m_bManualRobot11;
	afx_msg void OnBnClickedCheckBall();
	afx_msg BOOL OnMouseWheel(UINT nFlags, short zDelta, CPoint pt);
	CDualSlider m_sliderSimulationStep;
	afx_msg BOOL OnSetCursor(CWnd* pWnd, UINT nHitTest, UINT message);
	CComboBox m_comboCameraID;
	afx_msg void OnSelchangeComboCameraId();
	BOOL m_bBallSimulation;
	BOOL m_bRobotInfoSave;
	afx_msg void OnBnClickedCheckRobotInfoSave();
	CComboBox m_comboRobotTestBehavior;
	afx_msg void OnSelchangeComboRobotTestBehavior();
	afx_msg void OnBnClickedCheckCameraImageOnGame();
	BOOL m_bCameraImageOnGame;
};
