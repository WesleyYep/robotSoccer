
// RobotSoccerProgramDlg.cpp : implementation file
//

#include "stdafx.h"
#include "RobotSoccerProgram.h"
#include "RobotSoccerProgramDlg.h"
#include "afxdialogex.h"
#include <cctype>
#include <string>

#define _USE_MATH_DEFINES
#define WM_MY_THREAD_MESSAGE	WM_APP+100
#include <math.h>

#ifdef _DEBUG
#define new DEBUG_NEW
#endif


// CAboutDlg dialog used for App About

class CAboutDlg : public CDialogEx
{

public:
	CAboutDlg();

// Dialog Data
	enum { IDD = IDD_ABOUTBOX };

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support

// Implementation
protected:
	DECLARE_MESSAGE_MAP()
};

CAboutDlg::CAboutDlg() : CDialogEx(CAboutDlg::IDD)
{
}

void CAboutDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialogEx::DoDataExchange(pDX);
}

BEGIN_MESSAGE_MAP(CAboutDlg, CDialogEx)
//	ON_WM_DRAWITEM()
//	ON_WM_PAINT()
END_MESSAGE_MAP()


// CRobotSoccerProgramDlg dialog


CRobotSoccerProgramDlg::CRobotSoccerProgramDlg(CWnd* pParent /*=NULL*/)
	: CDialogEx(CRobotSoccerProgramDlg::IDD, pParent)
	, m_bManualControl(FALSE)
	, m_bManualRobotAll(TRUE)
	, m_bManualRobot1(FALSE)
	, m_bManualRobot2(FALSE)
	, m_bManualRobot3(FALSE)
	, m_bManualRobot4(FALSE)
	, m_bManualRobot5(FALSE)
	, m_bManualRobot6(FALSE)
	, m_bManualRobot7(FALSE)
	, m_bManualRobot8(FALSE)
	, m_bManualRobot9(FALSE)
	, m_bManualRobot10(FALSE)
	, m_bManualRobot11(FALSE)
	, m_bSimulationMode(FALSE)
	, m_bBallSimulation(FALSE)
	, m_strCOMPort(_T(""))
	, m_strProjectName(_T(""))
	, m_bRobotInfoSave(FALSE)
{
	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);

	m_bChangeCamera = false;
	m_bExit = false;
	m_bMouseCapture = false;

	m_pLookUpTable = NULL;

	m_bTransmitter = false;

	m_fpRobotInfo  = NULL;

	m_RobotTestBehavior = 0;
}

void CRobotSoccerProgramDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialogEx::DoDataExchange(pDX);
	DDX_Check(pDX, IDC_CHECK_ROBOT_ALL, m_bManualRobotAll);
	DDX_Check(pDX, IDC_CHECK_ROBOT1, m_bManualRobot1);
	DDX_Check(pDX, IDC_CHECK_ROBOT2, m_bManualRobot2);
	DDX_Check(pDX, IDC_CHECK_ROBOT3, m_bManualRobot3);
	DDX_Check(pDX, IDC_CHECK_SIMULATION, m_bSimulationMode);
	DDX_Control(pDX, IDC_COMBO_COM_PORT, m_comboCOMPort);
	DDX_CBString(pDX, IDC_COMBO_COM_PORT, m_strCOMPort);
	DDX_Check(pDX, IDC_CHECK_TRANSMITTER_TEST, m_bManualControl);
	DDX_Text(pDX, IDC_EDIT_FILE_NAME_SYSTEM, m_strProjectName);
	DDX_Check(pDX, IDC_CHECK_ROBOT4, m_bManualRobot4);
	DDX_Check(pDX, IDC_CHECK_ROBOT5, m_bManualRobot5);
	DDX_Check(pDX, IDC_CHECK_ROBOT6, m_bManualRobot6);
	DDX_Check(pDX, IDC_CHECK_ROBOT7, m_bManualRobot7);
	DDX_Check(pDX, IDC_CHECK_ROBOT8, m_bManualRobot8);
	DDX_Check(pDX, IDC_CHECK_ROBOT9, m_bManualRobot9);
	DDX_Check(pDX, IDC_CHECK_ROBOT12, m_bManualRobot10);
	DDX_Check(pDX, IDC_CHECK_ROBOT13, m_bManualRobot11);
	DDX_Control(pDX, IDC_PROGRESS_SLIDER_SIMULATION_STEP, m_sliderSimulationStep);
	DDX_Control(pDX, IDC_COMBO_CAMERA_ID, m_comboCameraID);
	DDX_Check(pDX, IDC_CHECK_BALL, m_bBallSimulation);
	DDX_Check(pDX, IDC_CHECK_ROBOT_INFO_SAVE, m_bRobotInfoSave);
	DDX_Control(pDX, IDC_COMBO_ROBOT_TEST_BEHAVIOR, m_comboRobotTestBehavior);
	DDX_Check(pDX, IDC_CHECK_CAMERA_IMAGE_ON_GAME, m_bCameraImageOnGame);

	//storing port number into a variable
	DDX_Text(pDX, IDC_EDIT_PORT_NUMBER, m_portNumber);
	DDX_Text(pDX, IDC_EDIT_CONNECTION_STATUS, m_connectionStatus);
}

BEGIN_MESSAGE_MAP(CRobotSoccerProgramDlg, CDialogEx)

	ON_MESSAGE(WM_MY_THREAD_MESSAGE, OnThreadMessage)
	ON_WM_SYSCOMMAND()
	ON_WM_PAINT()
	ON_WM_QUERYDRAGICON()
	ON_BN_CLICKED(IDC_OPEN_ROBOTSOCCER_SYSTEM, &CRobotSoccerProgramDlg::OnBnClickedOpenRobotsoccerSystem)
	ON_WM_DESTROY()
	ON_WM_LBUTTONDOWN()
	ON_WM_LBUTTONUP()
	ON_WM_RBUTTONDOWN()
	ON_WM_RBUTTONUP()
	ON_WM_MOUSEMOVE()
	ON_BN_CLICKED(IDCANCEL, &CRobotSoccerProgramDlg::OnBnClickedCancel)
	ON_BN_CLICKED(IDC_CHECK_ROBOT_ALL, &CRobotSoccerProgramDlg::OnBnClickedCheckRobotAll)
	ON_BN_CLICKED(IDC_CHECK_ROBOT1, &CRobotSoccerProgramDlg::OnBnClickedCheckRobot1)
	ON_BN_CLICKED(IDC_CHECK_ROBOT2, &CRobotSoccerProgramDlg::OnBnClickedCheckRobot2)
	ON_BN_CLICKED(IDC_CHECK_ROBOT3, &CRobotSoccerProgramDlg::OnBnClickedCheckRobot3)
	ON_BN_CLICKED(IDC_CHECK_SIMULATION, &CRobotSoccerProgramDlg::OnBnClickedCheckSimulation)
	ON_BN_CLICKED(IDC_CHECK_TRANSMITTER_TEST, &CRobotSoccerProgramDlg::OnBnClickedCheckTransmitterTest)
	ON_CBN_SELCHANGE(IDC_COMBO_COM_PORT, &CRobotSoccerProgramDlg::OnSelchangeComboComPort)
	ON_BN_CLICKED(IDC_SAVE_ROBOTSOCCER_SYSTEM, &CRobotSoccerProgramDlg::OnBnClickedSaveRobotsoccerSystem)
	ON_BN_CLICKED(IDC_CHECK_ROBOT4, &CRobotSoccerProgramDlg::OnBnClickedCheckRobot4)
	ON_BN_CLICKED(IDC_CHECK_ROBOT5, &CRobotSoccerProgramDlg::OnBnClickedCheckRobot5)
	ON_BN_CLICKED(IDC_CHECK_ROBOT6, &CRobotSoccerProgramDlg::OnBnClickedCheckRobot6)
	ON_BN_CLICKED(IDC_CHECK_ROBOT7, &CRobotSoccerProgramDlg::OnBnClickedCheckRobot7)
	ON_BN_CLICKED(IDC_CHECK_ROBOT8, &CRobotSoccerProgramDlg::OnBnClickedCheckRobot8)
	ON_BN_CLICKED(IDC_CHECK_ROBOT9, &CRobotSoccerProgramDlg::OnBnClickedCheckRobot9)
	ON_BN_CLICKED(IDC_CHECK_ROBOT12, &CRobotSoccerProgramDlg::OnBnClickedCheckRobot12)
	ON_BN_CLICKED(IDC_CHECK_ROBOT13, &CRobotSoccerProgramDlg::OnBnClickedCheckRobot13)
	ON_BN_CLICKED(IDC_CHECK_BALL, &CRobotSoccerProgramDlg::OnBnClickedCheckBall)
	ON_WM_MOUSEWHEEL()
	ON_WM_SETCURSOR()
	ON_CBN_SELCHANGE(IDC_COMBO_CAMERA_ID, &CRobotSoccerProgramDlg::OnSelchangeComboCameraId)
	ON_BN_CLICKED(IDC_CHECK_ROBOT_INFO_SAVE, &CRobotSoccerProgramDlg::OnBnClickedCheckRobotInfoSave)
	ON_CBN_SELCHANGE(IDC_COMBO_ROBOT_TEST_BEHAVIOR, &CRobotSoccerProgramDlg::OnSelchangeComboRobotTestBehavior)
	ON_BN_CLICKED(IDC_CHECK_CAMERA_IMAGE_ON_GAME, &CRobotSoccerProgramDlg::OnBnClickedCheckCameraImageOnGame)

	//connecting to port 
	ON_BN_CLICKED(IDC_CONNECT_TO_PORT, &CRobotSoccerProgramDlg::OnBnClickedConnectToHost)
END_MESSAGE_MAP()


// CRobotSoccerProgramDlg message handlers

BOOL CRobotSoccerProgramDlg::OnInitDialog()
{
	CDialogEx::OnInitDialog();

	// Add "About..." menu item to system menu.

	// IDM_ABOUTBOX must be in the system command range.
	ASSERT((IDM_ABOUTBOX & 0xFFF0) == IDM_ABOUTBOX);
	ASSERT(IDM_ABOUTBOX < 0xF000);

	CMenu* pSysMenu = GetSystemMenu(FALSE);
	if (pSysMenu != NULL)
	{
		BOOL bNameValid;
		CString strAboutMenu;
		bNameValid = strAboutMenu.LoadString(IDS_ABOUTBOX);
		ASSERT(bNameValid);
		if (!strAboutMenu.IsEmpty())
		{
			pSysMenu->AppendMenu(MF_SEPARATOR);
			pSysMenu->AppendMenu(MF_STRING, IDM_ABOUTBOX, strAboutMenu);
		}
	}

	// Set the icon for this dialog.  The framework does this automatically
	//  when the application's main window is not a dialog
	SetIcon(m_hIcon, TRUE);			// Set big icon
	SetIcon(m_hIcon, FALSE);		// Set small icon

	if( ROBOT_SOCCER_RROGRAM_ROBOT_N == ROBOT_SOCCER_RROGRAM_ROBOT_3 )
	{
		GetDlgItem(IDC_CHECK_ROBOT4)->ShowWindow(SW_HIDE);
		GetDlgItem(IDC_CHECK_ROBOT5)->ShowWindow(SW_HIDE);
	}
	if( ROBOT_SOCCER_RROGRAM_ROBOT_N == ROBOT_SOCCER_RROGRAM_ROBOT_5 )
	{
		GetDlgItem(IDC_CHECK_ROBOT4)->ShowWindow(SW_SHOW);
		GetDlgItem(IDC_CHECK_ROBOT5)->ShowWindow(SW_SHOW);
	}

	if( ROBOT_SOCCER_PROGRAM_COMMUNICATION_PROTOCOL == ROBOT_SOCCER_PROGRAM_COMMUNICATION_PROTOCOL_RIT
	 || ROBOT_SOCCER_PROGRAM_COMMUNICATION_PROTOCOL == ROBOT_SOCCER_PROGRAM_COMMUNICATION_PROTOCOL_KMC )
	{
		m_comboRobotTestBehavior.AddString( _T("forward") );
		m_comboRobotTestBehavior.AddString( _T("rotation") );
		m_comboRobotTestBehavior.SetCurSel(0);
	}

	if( ROBOT_SOCCER_PROGRAM_COMMUNICATION_PROTOCOL == ROBOT_SOCCER_PROGRAM_COMMUNICATION_PROTOCOL_MINIROBOT )
	{
		m_comboRobotTestBehavior.AddString( _T("0 ")                  );   
		m_comboRobotTestBehavior.AddString( _T("1 Å°ÆÛ¿ÞÂÊ¸·±â")       );   
		m_comboRobotTestBehavior.AddString( _T("2 Å°ÆÛÁ¤¸é¸·±â")       );   
		m_comboRobotTestBehavior.AddString( _T("3 Å°ÆÛ¿À¸¥ÂÊ¸·±â")     );     
		m_comboRobotTestBehavior.AddString( _T("4 ½Â¸® ¼¼·¹¸ð´Ï")      );    
		m_comboRobotTestBehavior.AddString( _T("5 ÀüÁø´Þ¸®±â50")       );  
		m_comboRobotTestBehavior.AddString( _T("6 ÀüÁø´Þ¸®±â30")       );  
		m_comboRobotTestBehavior.AddString( _T("7 ¿ÞÂÊÅÏ10")           );  
		m_comboRobotTestBehavior.AddString( _T("8 ¿ÞÂÊÅÏ20")           );  
		m_comboRobotTestBehavior.AddString( _T("9 ¿ÞÂÊÅÏ45")           );  
		m_comboRobotTestBehavior.AddString( _T("10 ¿ÞÂÊÅÏ60")          );  
		m_comboRobotTestBehavior.AddString( _T("11 ¿À¸¥ÂÊÅÏ10")        );  
		m_comboRobotTestBehavior.AddString( _T("12 ¿À¸¥ÂÊÅÏ20")        );  
		m_comboRobotTestBehavior.AddString( _T("13 ¿À¸¥ÂÊÅÏ45")        );  
		m_comboRobotTestBehavior.AddString( _T("14 ¿À¸¥ÂÊÅÏ60")        );  
		m_comboRobotTestBehavior.AddString( _T("15 ¿À¸¥ÂÊ¿·À¸·Î20")    );      
		m_comboRobotTestBehavior.AddString( _T("16 ¿À¸¥ÂÊ¿·À¸·Î70¿¬¼Ó"));          
		m_comboRobotTestBehavior.AddString( _T("17 ¿ÞÂÊ¿·À¸·Î20")      );   
		m_comboRobotTestBehavior.AddString( _T("18 ¿ÞÂÊ¿·À¸·Î70¿¬¼Ó")  );        
		m_comboRobotTestBehavior.AddString( _T("19 ÈÄÁø´Þ¸®±â40")      );   
		m_comboRobotTestBehavior.AddString( _T("20 ")                 );   
		m_comboRobotTestBehavior.AddString( _T("21 ÈÄÁøÁ¾Á¾°ÉÀ½")      );    
		m_comboRobotTestBehavior.AddString( _T("22 ¿À¸¥¹ß°øÂ÷±â")      );    
		m_comboRobotTestBehavior.AddString( _T("23 ¿Þ¹ß°øÂ÷±â")        );  
		m_comboRobotTestBehavior.AddString( _T("24 ¿À¸¥ÂÊÀ¸·Î½´ÆÃ")    );      
		m_comboRobotTestBehavior.AddString( _T("25 ¿ÞÂÊÀ¸·Î½´ÆÃ")      );    
		m_comboRobotTestBehavior.AddString( _T("26 ¿À¸¥¹ßµÚ½´ÆÃ")      );    
		m_comboRobotTestBehavior.AddString( _T("27 ¿Þ¹ßµÚ½´ÆÃ")        );  
		m_comboRobotTestBehavior.AddString( _T("28 ¿À¸¥ÂÊ¸·±â")        );  
		m_comboRobotTestBehavior.AddString( _T("29 ¿ÞÂÊ¸·±â")          );  
		m_comboRobotTestBehavior.AddString( _T("30 ÀüÁøÁ¾Á¾°ÉÀ½")      );    
		m_comboRobotTestBehavior.AddString( _T("31 ")                 );
		m_comboRobotTestBehavior.AddString( _T("32 Á¤Áö")              );
		m_comboRobotTestBehavior.SetCurSel(32);
	}

	{
		FILE *fp;
		fp = fopen("debug.cfg", "rt");

		if( fp != NULL )
		{
			while( !feof(fp) )
			{
				char buf[256];
				fgets( buf, 256, fp);

				CString patch_type(buf);

				while( patch_type.GetLength() >= 1 )
				{
					if( patch_type.GetAt(0) == ' '
						|| patch_type.GetAt(0) == '\t'
						|| patch_type.GetAt(0) == '\n'
						|| patch_type.GetAt(0) == '\r' )
						patch_type.Delete( 0, 1 );
					else
						break;
				}

				while(1)
				{
					int n = patch_type.GetLength();

					if( n <= 0 )
						break;

					if( patch_type.GetAt(n-1) == ' '
						|| patch_type.GetAt(n-1) == '\t'
						|| patch_type.GetAt(n-1) == '\n'
						|| patch_type.GetAt(n-1) == '\r' )
						patch_type.Delete( n-1, 1 );
					else
						break;
				}

				if( patch_type.GetLength() <= 0 )
					continue;

				if( patch_type.GetAt(0) == '/' && patch_type.GetAt(1) == '/' )
				{
					// commnet
				}
				else
				{
					if( patch_type == "game:record" )
					{
						GetDlgItem(IDC_CHECK_CAMERA_IMAGE_ON_GAME)->ShowWindow(SW_SHOW);
					}
				}
			}

			fclose( fp );
		}
	}

	Initialization();

	m_sliderSimulationStep.SetOption( _T("Simulation Accuracy"), false, 0, 50, 1, 0 );
	m_sliderSimulationStep.SetValue( 5 );

	return TRUE;  // return TRUE  unless you set the focus to a control
}

void CRobotSoccerProgramDlg::OnSysCommand(UINT nID, LPARAM lParam)
{
	if ((nID & 0xFFF0) == IDM_ABOUTBOX)
	{
		CAboutDlg dlgAbout;
		dlgAbout.DoModal();
	}
	else
	{
		CDialogEx::OnSysCommand(nID, lParam);
	}
}

// If you add a minimize button to your dialog, you will need the code below
//  to draw the icon.  For MFC applications using the document/view model,
//  this is automatically done for you by the framework.

void CRobotSoccerProgramDlg::OnPaint()
{
	if (IsIconic())
	{
		CPaintDC dc(this); // device context for painting

		SendMessage(WM_ICONERASEBKGND, reinterpret_cast<WPARAM>(dc.GetSafeHdc()), 0);

		// Center icon in client rectangle
		int cxIcon = GetSystemMetrics(SM_CXICON);
		int cyIcon = GetSystemMetrics(SM_CYICON);
		CRect rect;
		GetClientRect(&rect);
		int x = (rect.Width() - cxIcon + 1) / 2;
		int y = (rect.Height() - cyIcon + 1) / 2;

		// Draw the icon
		dc.DrawIcon(x, y, m_hIcon);
	}
	else
	{
		CDialogEx::OnPaint();
	}
}

// The system calls this function to obtain the cursor to display while the user drags
//  the minimized window.
HCURSOR CRobotSoccerProgramDlg::OnQueryDragIcon()
{
	return static_cast<HCURSOR>(m_hIcon);
}



void ImageGrabber_callback( void* main_class )
{
	((CRobotSoccerProgramDlg*)main_class)->Process();
}

bool m_bThreadDraw = false;
UINT DrawDisplay_callback(LPVOID pParam)
{
	while( m_bThreadDraw )
	{
		((CRobotSoccerProgramDlg*)pParam)->DrawDisplay();
	}

	return 0;
}

bool CRobotSoccerProgramDlg::Initialization(void)
{
	number = 0;

	isSending = false;
	m_comboCameraID.SetCurSel(0);

	{
		m_graphMain.SetRegionY( 0, 0.2 );
		m_graphMain.SetGridSize( 1, 0.05 );
		m_graphMain.SetTextFormatXAxis( _T("%.2lf") );
		m_graphMain.SetTextFormatYAxis( _T("%.3lf") );

		m_graphMain.SetGraphLabel( GRAPH_TIME_FRAME   , _T("Frame") );
		m_graphMain.SetGraphLabel( GRAPH_TIME_PROCESS , _T("Process") );
		m_graphMain.SetGraphLabel( GRAPH_TIME_DRAW    , _T("Draw") );
		m_graphMain.SetGraphLabel( GRAPH_TIME_VISION  , _T("Vision") );
		m_graphMain.SetGraphLabel( GRAPH_TIME_STRATEGY, _T("Strategy") );
	}

	/*
	std::vector<CPointDouble> vecTest;

	for( int i=0 ; i<1000000 ; i++ )
	{
		CPointDouble pos(1,1);

		vecTest.push_back( pos );
	}
	*/

	m_timeProgram.Start();

	m_ImageGrabber.Initialize();

	{
		// Display ¿µ¿ª ÃÊ±âÈ­
		CClientDC dc(this);
		CRect rcImage;
		GetDlgItem(IDC_FRAME_DISPLAY)->GetWindowRect(&rcImage);
		ScreenToClient(rcImage);
		GetDlgItem(IDC_FRAME_DISPLAY)->SetWindowPos( NULL, rcImage.left, rcImage.top, 640+8, 480+8, SWP_NOZORDER | SWP_NOACTIVATE);
		CRect rcDisplay( rcImage.left+4, rcImage.top+4, rcImage.left+640+4, rcImage.top+480+4 );
		m_dcDisplay.Init( &dc, rcDisplay );



		CRect rcGraph;
		GetDlgItem(IDC_FRAME_GRAPH)->GetWindowRect(rcGraph);
		ScreenToClient(rcGraph);
		rcGraph.DeflateRect(4,4,4,4);
		m_graphMain.Init( &dc, rcGraph );
	}

	{
		//m_ground.SetGround( CGround::_5vs5, CRect(0,0,640,480), 180 );

		m_SynchronousModule.SetGround( &m_ground );

		m_pLookUpTable = NULL;
		m_pLookUpTable = new CLookUpTable;

		m_propColorSetting.SetLookUpTable( m_pLookUpTable );
		m_propColorSetting.SetDisplayDC( &m_dcDisplay );
		m_propColorSetting.SetImageGrabber( &m_ImageGrabber );

		m_propVision.SetLookUpTable( m_pLookUpTable );
		m_propVision.SetDisplayDC( &m_dcDisplay );
		m_propVision.SetImageGrabber( &m_ImageGrabber );
		m_propVision.SetGround( &m_ground );

		m_propGame.SetImageGrabber( &m_ImageGrabber );
		m_propGame.SetDisplayDC( &m_dcDisplay );
	
		m_propStrategyGUI.SetDisplayDC( &m_dcDisplay );
	}


	m_PropertySheet.AddPage(&m_propColorSetting);	
	m_PropertySheet.AddPage(&m_propVision);	
	m_PropertySheet.AddPage(&m_propGame);	
	m_PropertySheet.AddPage(&m_propStrategyGUI);	

	m_PropertySheet.Create(this, WS_CHILD|WS_VISIBLE,0);
	m_PropertySheet.ModifyStyleEx(0, WS_EX_CONTROLPARENT);
	m_PropertySheet.ModifyStyle(WS_TABSTOP, 0);
	m_PropertySheet.SetTitle(_T("Property Sheet"), PSH_PROPTITLE);

	CRect rcSheet;
	GetDlgItem(IDC_FRAME_PROPERTY_SHEET)->GetWindowRect(&rcSheet);
	ScreenToClient(&rcSheet);
	CTabCtrl *TabCtrl = m_PropertySheet.GetTabControl();
	TabCtrl->MoveWindow(0, 0, rcSheet.Width(), rcSheet.Height());

	m_PropertySheet.SetWindowPos(NULL, rcSheet.left, rcSheet.top, rcSheet.Width(), rcSheet.Height(),
		SWP_NOZORDER | SWP_NOACTIVATE);
	m_PropertySheet.SetActivePage(3);	// for InitDialog execution
	m_PropertySheet.SetActivePage(2);	// for InitDialog execution
	m_PropertySheet.SetActivePage(1);	// for InitDialog execution
	m_PropertySheet.SetActivePage(0);	// for InitDialog execution

	m_propColorSetting.IsActive();

	if( m_ImageGrabber.IsReady() )
	{
		m_ImageGrabber.HookFunction( ImageGrabber_callback, this );
		
		m_bThreadDraw = true;
		m_pThreadDraw = AfxBeginThread( DrawDisplay_callback, this, THREAD_PRIORITY_NORMAL, 0, CREATE_SUSPENDED);
		m_pThreadDraw->m_bAutoDelete = true;
		m_pThreadDraw->ResumeThread();
		
		m_comboCOMPort.SetCurSel(0);
		InitializeSerialPort(1);
	}
	else
	{
		AfxMessageBox(_T("Unable to initialize camera.\nprogram does not work."), MB_ICONEXCLAMATION|MB_OK);

		return false;
	}

	CEdit *Number = reinterpret_cast<CEdit *>(GetDlgItem(IDC_EDIT_PORT_NUMBER));
	Number->SetWindowText( _T("31000") );

	return false;
}

static UINT DataReceivingThread(void*);
//CONNECTTOHOST – Connects to a remote host
void CRobotSoccerProgramDlg::OnBnClickedConnectToHost() {
	
	//variable to store the control
	CEdit *Display; //connection status
	CEdit *Number;	//text field

	//getting reference of the controls
	Display = reinterpret_cast<CEdit *>(GetDlgItem(IDC_EDIT_CONNECTION_STATUS));
	Number = reinterpret_cast<CEdit *>(GetDlgItem(IDC_EDIT_PORT_NUMBER));

	//getting the string from the text field
	Number->GetWindowText(m_portNumber);
	
	//checking if the string is number only
	has_only_digit = true;

	for (int n = 0; n < m_portNumber.GetLength(); n++) {
		if (!std::isdigit( m_portNumber[ n ] )) {
			has_only_digit = false;
			break;
		}
	}  

	//return an error if the string is not number only
	if (has_only_digit == false || m_portNumber.GetLength() == 0) {
		Display->SetWindowText( _T("Input Error") );
		return;
	}

	//convert CString into int 
	//NOTE: don't know how to error handle +tstoi yet so I check if it's 
	//correct format beforehand.
	int portNumber = _tstoi(m_portNumber);

	//connect to the host and display the status 
	if (connectToHost(portNumber) == true) {
		Display->SetWindowText( _T("Connected") );
	}else {
		Display->SetWindowText( _T("Error") );
	}
}


SOCKET CRobotSoccerProgramDlg::getSocket() {
	if (s != NULL) {
		return s;
	}
}

bool CRobotSoccerProgramDlg::connectToHost(int portNumber)
{
	//Start up Winsock…
	WSADATA wsadata;

	int error = WSAStartup(0x0202, &wsadata);

	//Did something happen?
	if (error)
		return false;

	//Did we get the right Winsock version?
	if (wsadata.wVersion != 0x0202)
	{
		WSACleanup(); //Clean up Winsock
		return false;
	}

	//Fill out the information needed to initialize a socket…
	SOCKADDR_IN target; //Socket address information

	target.sin_family = AF_INET; // address family Internet
	target.sin_port = htons (portNumber); //Port to connect on
	target.sin_addr.s_addr = inet_addr ("127.0.0.1"); //Target IP

	s = socket (AF_INET, SOCK_STREAM, IPPROTO_TCP); //Create socket
	
	//check if the socket is valid
	if (s == INVALID_SOCKET) {
		WSACleanup();
		s = -1;
		return false;
	}

	error = connect(s, (SOCKADDR *)&target, sizeof(target));
	
	//check if the socket is able to the server
	if (error == SOCKET_ERROR) {
		closeConnection();
		return false;
	}
	AfxBeginThread(DataReceivingThread, this);

	return true;
}

void CRobotSoccerProgramDlg::sendStuff(int data){
	if (s) {
		int b = htonl(data);
		const char* bytesOfi = (const char*)&b;

		CEdit *Display;
		Display = reinterpret_cast<CEdit *>(GetDlgItem(IDC_EDIT_CONNECTION_STATUS));
		int iResult = send(s, bytesOfi, sizeof(data), 0);

		if (iResult == SOCKET_ERROR) {
			Display->SetWindowText( _T("send fail with error") );
			closeConnection();
		}
	}
}

std::string CRobotSoccerProgramDlg::receiveStuff() {
	//creating char* buffer
	char buffer[512];
//	buffer[0] = 0;
	int len = 512;
	int iResult = recv(s2, buffer,  len, 0);
	if (iResult > 0) {
		char* lol = buffer;
		std::string hello(lol);
		return hello;
	} else {
		return "";
	}
}

void CRobotSoccerProgramDlg::sendStuff(std::string data) {

	//creating char* buffer
	char buffer[512];
	buffer[0] = 0;
	bool hasNewLine = false;

	//checking if the message has newline character at the end if not add it in the end
	//this is because java readLine() require newline character to know the end of each line
	if (data[data.length()-1] == '\n') {
		hasNewLine = true;
	}
	else {
		data.append("\n");
	}

	//putting the string into the buffer
	sprintf_s(buffer, data.c_str()); 

	CEdit *Display;
	Display = reinterpret_cast<CEdit *>(GetDlgItem(IDC_EDIT_CONNECTION_STATUS));
	int len = strlen(buffer);
	int iResult = send(s, buffer, len, 0);


	if (iResult == SOCKET_ERROR) {
			Display->SetWindowText( _T("send fail with error") );
			closeConnection();
	}
}

//CLOSECONNECTION – shuts down the socket and closes any connection on it
void CRobotSoccerProgramDlg::closeConnection ()
{
	//Close the socket if it exists
	if (s)
		closesocket(s);
		WSACleanup(); //Clean up Winsock
}


void CRobotSoccerProgramDlg::Process(void)
{
//	m_ground.Set

	m_SynchronousModule.SetDelayTime(2);


	m_timeProgram.Check();

	m_timeFrame.Stop();
	m_graphMain.AddData( GRAPH_TIME_FRAME   , m_timeProgram.GetTimeSec(), m_timeFrame.GetTimeSec() );
	m_timeFrame.Start();

	m_timeProcess.Start();


	/*
	if( m_propStrategyGUI.IsActive() )
	{
	}
	*/

	if( m_propGame.IsActive() )
	{
		m_VelocityInfo = m_SynchronousModule.VelocityData();

		if( m_bSimulationMode == FALSE )
			SendVelocity();

		m_SynchronousModule.TimePass();
	}
	else
	{
		//need to be unedited
		m_VelocityInfo.Reset();

		if( m_bSimulationMode == FALSE )
			SendVelocity();
	}

	//Color setting page
	if( m_propColorSetting.IsActive() )
	{
		m_propColorSetting.GenerateMaskInformation();
	}
	else
	//vision setting page
	if( m_propVision.IsActive() )
	{
		m_propVision.VisionRun();
		m_propVision.GenerateDrawInformation();
	}
	else
	//establish stratgy page
	if( m_propStrategyGUI.IsActive() )
	{
		m_propStrategyGUI.GenerateDrawInformation();
	}
	else
	//game operation stratgy exec page
	if( m_propGame.IsActive() )
	{
		if( m_propGame.m_bCopyStrategyGUI )
		{
			m_propGame.m_bCopyStrategyGUI = false;
			EnterCriticalSection( &m_propGame.m_strategyGUI.m_critStrategyGUI );
			CRITICAL_SECTION crit_backup = m_propGame.m_strategyGUI.m_critStrategyGUI;
			m_propGame.m_strategyGUI = m_propStrategyGUI.m_strategyGUI;
			m_propGame.m_strategyGUI.m_critStrategyGUI = crit_backup;
			LeaveCriticalSection( &m_propGame.m_strategyGUI.m_critStrategyGUI );

			m_propGame.UpdateSetPlayRole();
		}

		if( m_bSimulationMode == FALSE )
		{
			m_timeVision.Start();
			m_propVision.VisionRun();
			m_timeVision.Stop();
			if( m_bCameraImageOnGame )
				m_propVision.GenerateDrawInformation();
		}

		m_timeStrategy.Start();

		if( m_bSimulationMode == FALSE )
		{
			m_ObjectInfo = m_propVision.GetObjectInfo();
		}
		else
		{
			m_ObjectInfo.m_Ball.state = OBJECT_STATE_VIRTUAL;
			m_ObjectInfo.m_Ball.countLost = 0;
			
			for( int i=0 ; i<POSITION_INFO_ROBOT_N ; i++ )
			{
				m_ObjectInfo.m_Robot[i].state = OBJECT_STATE_VIRTUAL;
				m_ObjectInfo.m_Robot[i].countLost = 0;
				m_ObjectInfo.m_Opp[i].state   = OBJECT_STATE_VIRTUAL;
				m_ObjectInfo.m_Opp[i].countLost = 0;
			}
		}

		
		if( m_bBallSimulation == TRUE )
		{
			m_ObjectInfo.m_Ball.state = OBJECT_STATE_VIRTUAL;
			m_ObjectInfo.m_Ball.countLost = 0;
		}

		CObjectPositionInfo info;
		info = m_propGame.GetObjectInfomation();

		/*if (isSending == true) {
			info = listener.GetObjectInfomation();
		}*/

		m_SynchronousModule.UpdatePositionData( m_ObjectInfo, info );

		if( m_bBallSimulation == TRUE || m_bSimulationMode == TRUE )
		{
			//broke here
			m_SynchronousModule.Simulation(FALSE, (int)m_sliderSimulationStep.GetValue() );
		}
		else
		{
			m_SynchronousModule.Simulation(TRUE, (int)m_sliderSimulationStep.GetValue());
		}

		m_ObjectInfo = m_SynchronousModule.PositionData();
		
		CObjectPositionInfo current = m_SynchronousModule.PositionDataCurrent();
		CObjectPositionInfo past = m_SynchronousModule.PositionDataPast();
		CObjectPositionInfo error = m_SynchronousModule.PositionDataError();

		double x, y, theta;
		for (int i = 0; i < 5; i++) {
			x = current.m_Robot[i].pos.x;
			y = current.m_Robot[i].pos.y;
			theta = current.m_Robot[i].orientation;

			

			char test[512];
			test[0] = 0;
			int n;
			n = sprintf(test,"Robot id=%d x=%f y=%f theta=%f", i,x,y,theta);
			std::string message(test);
			sendStuff(message);
			//sendStuff("Robot id=" + std::to_string(i) + " x=" + std::to_string(x) + " y=" + std::to_string(y) 
			//	+ " theta=" + std::to_string(theta));
		}
		
		double ball_xPos = (m_ObjectInfo.m_Ball.pos.x); //we know its the ball by adding 10000
		double ball_yPos = (m_ObjectInfo.m_Ball.pos.y);

		char test[512];
		test[0] = 0;
		int n;
		n = sprintf(test,"Ball x=%f y=%f ",ball_xPos,ball_yPos);
		std::string message(test);
		sendStuff(message);
		

		m_propGame.SetObjectInfomation( m_ObjectInfo, current, past, error );
		
		m_propGame.GameRun( m_timeProgram.GetTimeSec() );

		
		m_VelocityInfo = m_propGame.Velocity();


		if( m_bRobotInfoSave )
		{
			fprintf( m_fpRobotInfo, "%.4lf\t%.4lf\t%.4lf\t%.4lf\t%.4lf\t%.4lf\t%.4lf\t%.4lf\t%.4lf\t\n"
				, current.m_Robot[0].pos.x
				, current.m_Robot[0].pos.y
				, current.m_Robot[0].orientation
				, current.m_Robot[1].pos.x
				, current.m_Robot[1].pos.y
				, current.m_Robot[1].orientation
				, current.m_Robot[2].pos.x
				, current.m_Robot[2].pos.y
				, current.m_Robot[2].orientation );
		}

		
		if( m_bManualControl )
		{
			
			bool bRobotManualControl[11];
			bRobotManualControl[0] = (m_bManualRobotAll == TRUE || m_bManualRobot1 == TRUE );
			bRobotManualControl[1] = (m_bManualRobotAll == TRUE || m_bManualRobot2 == TRUE );
			bRobotManualControl[2] = (m_bManualRobotAll == TRUE || m_bManualRobot3 == TRUE );
			bRobotManualControl[3] = (m_bManualRobotAll == TRUE || m_bManualRobot4 == TRUE );
			bRobotManualControl[4] = (m_bManualRobotAll == TRUE || m_bManualRobot5 == TRUE );
			bRobotManualControl[5] = (m_bManualRobotAll == TRUE || m_bManualRobot6 == TRUE );
			bRobotManualControl[6] = (m_bManualRobotAll == TRUE || m_bManualRobot7 == TRUE );
			bRobotManualControl[7] = (m_bManualRobotAll == TRUE || m_bManualRobot8 == TRUE );
			bRobotManualControl[8] = (m_bManualRobotAll == TRUE || m_bManualRobot9 == TRUE );
			bRobotManualControl[9] = (m_bManualRobotAll == TRUE || m_bManualRobot10 == TRUE );
			bRobotManualControl[10] =(m_bManualRobotAll == TRUE || m_bManualRobot11 == TRUE );
			

			double v_linear  = 0.0;
			double v_angular = 0.0;
			
			if( ROBOT_SOCCER_PROGRAM_COMMUNICATION_PROTOCOL == ROBOT_SOCCER_PROGRAM_COMMUNICATION_PROTOCOL_RIT
			 || ROBOT_SOCCER_PROGRAM_COMMUNICATION_PROTOCOL == ROBOT_SOCCER_PROGRAM_COMMUNICATION_PROTOCOL_KMC )
			{
				if( m_RobotTestBehavior == 0 )
				{
					v_linear  = 0.1;
					v_angular = 0.0;
				}

				if( m_RobotTestBehavior == 1 )
				{
					v_linear  = 0.0;
					v_angular = M_PI/2; 
				}
			}

			double v_left  = v_linear - v_angular * 0.0675 / 2.;
			double v_right = v_linear + v_angular * 0.0675 / 2.;

			
			for( int i=0 ; i<11 ; i++ )
			{
				if( bRobotManualControl[i] )
				{
					/*
					m_VelocityInfo.m_VelocityLeft[i] = v_left;
					m_VelocityInfo.m_VelocityRight[i] = v_right;

					m_VelocityInfo.m_LinearVelocity[i] = v_linear;
					m_VelocityInfo.m_AngularVelocity[i] = v_angular;

					m_VelocityInfo.m_Behavior[i] = m_RobotTestBehavior;
						*/		
					m_cs.Lock();
					for( int i=0 ; i<11 ; i++ ){

							m_VelocityInfo.m_LinearVelocity[i] = javaVelocityInfo.m_LinearVelocity[i];
							m_VelocityInfo.m_AngularVelocity[i] = javaVelocityInfo.m_AngularVelocity[i];

							m_VelocityInfo.m_VelocityLeft[i] = javaVelocityInfo.m_LinearVelocity[i] - javaVelocityInfo.m_AngularVelocity[i] * 0.0675 / 2.;
							m_VelocityInfo.m_VelocityRight[i] = javaVelocityInfo.m_LinearVelocity[i] + javaVelocityInfo.m_AngularVelocity[i] * 0.0675 / 2.;

							m_VelocityInfo.m_Behavior[i] = 0;
					}
					m_cs.Unlock();

				}
				else
				{
					m_VelocityInfo.m_VelocityLeft[i]  = 0;
					m_VelocityInfo.m_VelocityRight[i] = 0;

					m_VelocityInfo.m_LinearVelocity[i]  = 0;
					m_VelocityInfo.m_AngularVelocity[i] = 0;

					m_VelocityInfo.m_Behavior[i] = ROBOT_BEHAVIOR_STOP; // stop behavior
				}
			} 	
		} 
		
		
		
		m_SynchronousModule.UpdateVelocityData( m_VelocityInfo );

		m_timeStrategy.Stop();
	}

	if( m_bExit && m_bThreadDraw == false)
	{
		{
			for( int i=0 ; i<VELOCITY_INFO_ROBOT_N ; i++ )
			{
				m_VelocityInfo.m_VelocityLeft[i] = 0;
				m_VelocityInfo.m_VelocityRight[i] = 0;
					
				m_VelocityInfo.m_LinearVelocity[i] = 0;
				m_VelocityInfo.m_AngularVelocity[i] = 0;
			}
			SendVelocity();
		}

		m_ImageGrabber.UnhookFunction();

		CDialogEx::OnCancel();
	}

	if( m_bChangeCamera )
	{
		m_bChangeCamera = false;
		
		
		if( m_cameraID == 4 )
		{
			CString pathname;

			TCHAR szFilter[] = _T("Image (*.bmp,*.jpg, *.gif, *.png, *.tif, *.mng, *.ico, *.pcx, *.tga, *.wmf, *.jbg, *.j2k)|*.bmp;*.jpg;*.gif;*.png;*.tif;*.mng;*.ico;*.pcx;*.tga;*.wmf;*.jbg;*.j2k|AVI Files(*.avi)|*.avi|All Files(*.*)|*.*||");
			CFileDialog fileDlg(TRUE, _T("default"), _T("default"), NULL, szFilter);
		
			if(IDOK == fileDlg.DoModal())
			{
				pathname = fileDlg.GetPathName();

				m_ImageGrabber.ChangeCamera( (CStringA)pathname );
			}
		}
		else
			m_ImageGrabber.ChangeCamera( m_cameraID );
		/*
		//m_ImageGrabber.Suspend();
		m_ImageGrabber.Free();

		if( m_cameraID == 0 || m_cameraID == 1 )
			m_ImageGrabber.Initialize( m_cameraID );
		else
			m_ImageGrabber.Initialize("");
		//m_ImageGrabber.Resume();
		*/
	}

	if( m_bSimulationMode == TRUE )
	{
		m_ImageGrabber.SimulationMode( true );
	}
	else
	{
		m_ImageGrabber.SimulationMode( false );
	}


	m_timeProcess.Stop();

	m_graphMain.AddData( GRAPH_TIME_VISION  , m_timeProgram.GetTimeSec(), m_timeVision.GetTimeSec()   );
	m_graphMain.AddData( GRAPH_TIME_PROCESS , m_timeProgram.GetTimeSec(), m_timeProcess.GetTimeSec()  );
	m_graphMain.AddData( GRAPH_TIME_STRATEGY, m_timeProgram.GetTimeSec(), m_timeStrategy.GetTimeSec() );
}

void CRobotSoccerProgramDlg::SendVelocity(void)
{
	if( m_bTransmitter )
	{
		if( ROBOT_SOCCER_PROGRAM_COMMUNICATION_PROTOCOL == ROBOT_SOCCER_PROGRAM_COMMUNICATION_PROTOCOL_RIT )
		{
			BYTE sdata[39]={0,};
			int tempvr,tempvl;
		
			sdata[0] = 0xff;
			sdata[1] = 0xff;
			sdata[2] = 0x01;
		
			for( int i=0 ; i<11 ; i++ )
			{
				tempvr=(int)(m_VelocityInfo.m_VelocityRight[i]*126);
				tempvl=(int)(m_VelocityInfo.m_VelocityLeft[i]*126);
				//tempvr = 0;
				//tempvl = 0;
			
				if( tempvr > 511 ) tempvr = 511;
				if( tempvr <-511 ) tempvr =-511;
				if( tempvl > 511 ) tempvl = 511;
				if( tempvl <-511 ) tempvl =-511;
			
				sdata[3*i  +3] = (BYTE)(tempvr & 0xff);
				sdata[3*i+1+3] = (BYTE)(tempvl & 0xff);
				sdata[3*i+2+3] = (BYTE)((tempvr & 0x0300) >> 8) + (BYTE)((tempvl & 0x0300) >> 6);
			}
		
			sdata[33+3] =	(BYTE)(27 & 0xff); 
			sdata[34+3] =	(BYTE)(2 & 0xff);
			sdata[35+3] =	(BYTE)(8 & 0xff);
		
			m_ComPort.WriteComm(sdata, 39);
		}

		if( ROBOT_SOCCER_PROGRAM_COMMUNICATION_PROTOCOL == ROBOT_SOCCER_PROGRAM_COMMUNICATION_PROTOCOL_KMC )
		{
			BYTE sdata[48]={0,};

			BYTE checksum = 0;

			double templinv, tempangv;
			sdata[0] = 0xff;
			sdata[1] = 0xff;
			sdata[2] = 0x01;

			checksum = sdata[0] + sdata[1] + sdata[2];

			for( int i=0 ; i<11 ; i++ )
			{
				templinv = m_VelocityInfo.m_LinearVelocity[i];
				tempangv = m_VelocityInfo.m_AngularVelocity[i];

				if     (templinv <   -4.) templinv = -4.;
				else if(templinv >    4.) templinv =  4.;
				if     (tempangv < -128.) tempangv = -128.;
				else if(tempangv >  128.) tempangv =  128.;

				sdata[4*i+0+3] = (BYTE)( ((int)(templinv*2048.)   ) & 0xff);
				sdata[4*i+1+3] = (BYTE)( ((int)(templinv*2048.)>>8) & 0xff);
				sdata[4*i+2+3] = (BYTE)( ((int)(tempangv*64.  )   ) & 0xff);
				sdata[4*i+3+3] = (BYTE)( ((int)(tempangv*64.  )>>8) & 0xff);

				checksum += sdata[4*i  +3];
				checksum += sdata[4*i+1+3];
				checksum += sdata[4*i+2+3];
				checksum += sdata[4*i+3+3];
			}
		
			sdata[47] =	-checksum; 

			m_ComPort.WriteComm(sdata, 48);
		}

		if( ROBOT_SOCCER_PROGRAM_COMMUNICATION_PROTOCOL == ROBOT_SOCCER_PROGRAM_COMMUNICATION_PROTOCOL_MINIROBOT )
		{
			BYTE SendString[30];
			SendString[0] = (BYTE)m_VelocityInfo.m_Behavior[0];
			m_ComPort.WriteComm(SendString, 1);

			SendString[0] = (BYTE)m_VelocityInfo.m_Behavior[1]+32;
			m_ComPort.WriteComm(SendString, 1);

			SendString[0] = (BYTE)m_VelocityInfo.m_Behavior[2]+64;
			m_ComPort.WriteComm(SendString, 1);
		}
	}
}

void CRobotSoccerProgramDlg::DrawDisplay(void)
{
	m_timeDraw.Start();

		if( m_propColorSetting.IsActive() )
		{
			CString strActiveVisionCore = m_propVision.GetActiveVisionCore();

			m_propColorSetting.SetActiveVisionCore( strActiveVisionCore );

			m_propColorSetting.DrawDisplay();
		}
		else
		if( m_propVision.IsActive() )
		{
			m_propVision.DrawDisplay();
		}
		else
		if( m_propStrategyGUI.IsActive() )
		{
			m_propStrategyGUI.DrawDisplay();
		}
		else
		if( m_propGame.IsActive() )
		{
			if( m_bCameraImageOnGame )
			{
				m_propVision.DrawDisplay();
			}
			else
			{
				m_propGame.DrawDisplay();
			
				{
					CRect text_rect(400, 5, 500, 20);
					if( m_bTransmitter )
					{
						m_dcDisplay.FillSolidRect( text_rect, RGB( 0,192,0) );
						m_dcDisplay.SetBkMode( TRANSPARENT );
						m_dcDisplay.SetTextColor( RGB( 255,255,255 ) );
						m_dcDisplay.DrawText( _T("   Transmitter : ON"), text_rect, DT_LEFT);
					}
					else
					{
						m_dcDisplay.FillSolidRect( text_rect, RGB( 255,64,64) );
						m_dcDisplay.SetBkMode( TRANSPARENT );
						m_dcDisplay.SetTextColor( RGB( 255,255,255 ) );
						m_dcDisplay.DrawText( _T("   Transmitter : OFF"), text_rect, DT_LEFT);
					}
				}

				{
					CRect text_rect(280, 5, 380, 20);
					if( m_bSimulationMode )
					{
						m_dcDisplay.FillSolidRect( text_rect, RGB( 64,64,255 ) );
						m_dcDisplay.SetBkMode( TRANSPARENT );
						m_dcDisplay.SetTextColor( RGB( 255,255,255 ) );
						m_dcDisplay.DrawText( _T("  Simulation Mode  "), text_rect, DT_CENTER);
					}
					else
					{
						m_dcDisplay.FillSolidRect( text_rect, RGB( 0,192,0) );
						m_dcDisplay.SetBkMode( TRANSPARENT );
						m_dcDisplay.SetTextColor( RGB( 255,255,255 ) );
						m_dcDisplay.DrawText( _T("    Camera Mode    "), text_rect, DT_CENTER);
					}
				}

				{
					CRect text_rect = CRect(-70, -10, 70, 10) + CPoint(320, 200);
					if( m_bBallSimulation )
					{
						m_dcDisplay.FillSolidRect( text_rect, RGB( 255,64,64) );
						m_dcDisplay.SetBkMode( TRANSPARENT );
						m_dcDisplay.SetTextColor( RGB( 255,255,255 ) );
						m_dcDisplay.DrawText( _T("  Ball Simulation  "), text_rect, DT_CENTER|DT_VCENTER|DT_SINGLELINE);
					}
				}
				{
					CRect text_rect = CRect(-70, -10, 70, 10) + CPoint(320, 200);
					if( m_bManualControl )
					{
						m_dcDisplay.FillSolidRect( text_rect, RGB( 255,64,64) );
						m_dcDisplay.SetBkMode( TRANSPARENT );
						m_dcDisplay.SetTextColor( RGB( 255,255,255 ) );
						m_dcDisplay.DrawText( _T("  Manual Control  "), text_rect, DT_CENTER|DT_VCENTER|DT_SINGLELINE);
					}
				}
			}
		}

		CClientDC dc(this);
		m_dcDisplay.BitBlt(&dc);

		DrawGraph();

	m_timeDraw.Stop();

	m_graphMain.AddData( GRAPH_TIME_DRAW, m_timeProgram.GetTimeSec(), m_timeDraw.GetTimeSec() );
	
	if( m_bExit )
	{
		if( m_bThreadDraw )
		if( m_pThreadDraw->m_hThread != NULL )
		{
			m_bThreadDraw = false;
			CloseHandle(m_pThreadDraw->m_hThread);   
			m_pThreadDraw->m_hThread = NULL;  
		}
	}
}


void CRobotSoccerProgramDlg::DrawGraph(void)
{
	CClientDC dc(this);

	m_graphMain.SetRegionX( m_timeProgram.GetTimeSec()-5, m_timeProgram.GetTimeSec() ); // ½Ã°£(t)¿¡ µû¶ó ¹Ù²ñ

	m_graphMain.DrawGraph();
	m_graphMain.BitBlt(&dc);
}

void CRobotSoccerProgramDlg::MouseCapture( MouseCapture::Enum capture )
{
	if( capture == MouseCapture::SET )
	{
		SetCapture();
		m_bMouseCapture = true;
	}
	if( capture == MouseCapture::RELEASE )
	{
		ReleaseCapture();
		m_bMouseCapture = false;
	}
}

void CRobotSoccerProgramDlg::OnLButtonDown(UINT nFlags, CPoint point)
{
	m_pointMouse = point;

	if( m_propColorSetting.IsActive() )
	{
		if( m_dcDisplay.IsPointInRect( m_pointMouse ) )
		{
			SetNewCursor( IDC_CURSOR_ZOOM2 );
		
			m_propColorSetting.DisplayLButtonDown( nFlags, m_pointMouse - m_dcDisplay.TopLeft() );
		}
	}
	else
	if( m_propVision.IsActive() )
	{
		if( m_dcDisplay.IsPointInRect( m_pointMouse ) )
		{
			if( m_propVision.m_bProcessingArea )
			{
				SetNewCursor( IDC_CURSOR_PROCESSING_AREA );
			}
			else
			{
				SetNewCursor( IDC_CURSOR_REVISION0 );
			}

			MouseCapture( m_propVision.DisplayLButtonDown( nFlags, m_pointMouse - m_dcDisplay.TopLeft() ) );
		}
	}
	else
	if( m_propGame.IsActive() )
	{
		if( m_dcDisplay.IsPointInRect( m_pointMouse ) )
		{
			MouseCapture( m_propGame.DisplayLButtonDown( nFlags, m_pointMouse - m_dcDisplay.TopLeft() ) );
		}
	}
	else
	if( m_propStrategyGUI.IsActive() )
	{
		if( m_dcDisplay.IsPointInRect( m_pointMouse ) )
		{
			MouseCapture( m_propStrategyGUI.DisplayLButtonDown( nFlags, m_pointMouse - m_dcDisplay.TopLeft() ) );
		}
	}

	CDialogEx::OnLButtonDown(nFlags, point);
}


void CRobotSoccerProgramDlg::OnLButtonUp(UINT nFlags, CPoint point)
{
	m_pointMouse = point;
	
	if( m_propColorSetting.IsActive() )
	{
		if( m_bMouseCapture || m_dcDisplay.IsPointInRect( m_pointMouse ) )
		{
			SetNewCursor( IDC_CURSOR_ZOOM1 );
		}
		else
		{
			SetNewCursor( (UINT)(IDC_ARROW) );
		}
	}
	else
	if( m_propVision.IsActive() )
	{
		if( m_bMouseCapture || m_dcDisplay.IsPointInRect( point ) )
		{
			if( m_propVision.m_bProcessingArea && !m_propVision.m_bRevisionImage && !m_propVision.m_bFlatImage )
			{
				SetNewCursor( IDC_CURSOR_PROCESSING_AREA );
			}
			else
			{
				SetNewCursor( IDC_CURSOR_REVISION0 );
			}

			MouseCapture( m_propVision.DisplayLButtonUp( nFlags, m_pointMouse - m_dcDisplay.TopLeft() ) );
		}
	}
	else
	if( m_propGame.IsActive() )
	{
		MouseCapture( m_propGame.DisplayLButtonUp( nFlags, m_pointMouse - m_dcDisplay.TopLeft() ) );
	}
	else
	if( m_propStrategyGUI.IsActive() )
	{
		MouseCapture( m_propStrategyGUI.DisplayLButtonUp( nFlags, m_pointMouse - m_dcDisplay.TopLeft() ) );
	}

	CDialogEx::OnLButtonUp(nFlags, point);
}



void CRobotSoccerProgramDlg::OnRButtonDown(UINT nFlags, CPoint point)
{
	m_pointMouse = point;

	if( m_propColorSetting.IsActive() )
	{

	}
	else
	if( m_propVision.IsActive() )
	{
		if( m_dcDisplay.IsPointInRect( m_pointMouse ) )
		{
			MouseCapture( m_propVision.DisplayRButtonDown( nFlags, m_pointMouse - m_dcDisplay.TopLeft() ) );
		}
	}
	else
	if( m_propGame.IsActive() )
	{
		if( m_dcDisplay.IsPointInRect( m_pointMouse ) )
		{
			MouseCapture( m_propGame.DisplayRButtonDown( nFlags, m_pointMouse - m_dcDisplay.TopLeft() ) );
		}
	}
	else
	if( m_propStrategyGUI.IsActive() )
	{
		if( m_dcDisplay.IsPointInRect( m_pointMouse ) )
		{
			MouseCapture( m_propStrategyGUI.DisplayRButtonDown( nFlags, m_pointMouse - m_dcDisplay.TopLeft() ) );
		}
	}

	CDialogEx::OnRButtonDown(nFlags, point);
}


void CRobotSoccerProgramDlg::OnRButtonUp(UINT nFlags, CPoint point)
{
	m_pointMouse = point;

	if( m_propColorSetting.IsActive() )
	{
	}
	else
	if( m_propVision.IsActive() )
	{
		if( m_bMouseCapture || m_dcDisplay.IsPointInRect( point ) )
		{
			MouseCapture( m_propVision.DisplayRButtonUp( nFlags, m_pointMouse - m_dcDisplay.TopLeft() ) );
		}
	}
	else
	if( m_propGame.IsActive() )
	{
		if( m_bMouseCapture || m_dcDisplay.IsPointInRect( m_pointMouse ) )
		{
			MouseCapture( m_propGame.DisplayRButtonUp( nFlags, m_pointMouse - m_dcDisplay.TopLeft() ) );
		}
	}
	else
	if( m_propStrategyGUI.IsActive() )
	{
		if( m_bMouseCapture || m_dcDisplay.IsPointInRect( m_pointMouse ) )
		{
			MouseCapture( m_propStrategyGUI.DisplayRButtonUp( nFlags, m_pointMouse - m_dcDisplay.TopLeft() ) );
		}
	}

	CDialogEx::OnRButtonUp(nFlags, point);
}


void CRobotSoccerProgramDlg::OnMouseMove(UINT nFlags, CPoint point)
{
	//static int checkTest = 0;
	//checkTest++;
	if( m_propColorSetting.IsActive() )
	{
		if( m_dcDisplay.IsPointInRect( point ) )
		{
			SetNewCursor( IDC_CURSOR_ZOOM1 );
		}
		else
		{
			SetNewCursor( (UINT)(IDC_ARROW) );
		}
	}
	else
	if( m_propVision.IsActive() )
	{
		if( m_dcDisplay.IsPointInRect( point ) )
		{
			if( m_propVision.m_bProcessingArea && !m_propVision.m_bRevisionImage && !m_propVision.m_bFlatImage )
			{
				SetNewCursor( IDC_CURSOR_PROCESSING_AREA );
			}
			else
			{
				//SetNewCursor( IDC_CURSOR_REVISION0 );
			}
		}
		else
		{
			SetNewCursor( (UINT)(IDC_ARROW) );
		}
		
		
		if( m_bMouseCapture || m_dcDisplay.IsPointInRect( point ) )
		{
			MouseCapture( m_propVision.DisplayMouseMove( nFlags, point - m_dcDisplay.TopLeft() ) );
		}
	}
	else
	if( m_propGame.IsActive() )
	{
		if( m_bMouseCapture || m_dcDisplay.IsPointInRect( point ) )
		{
			MouseCapture( m_propGame.DisplayMouseMove( nFlags, point - m_dcDisplay.TopLeft() ) );
		}
	}
	else
	if( m_propStrategyGUI.IsActive() )
	{
		if( m_bMouseCapture || m_dcDisplay.IsPointInRect( point ) )
		{
			MouseCapture( m_propStrategyGUI.DisplayMouseMove( nFlags, point - m_dcDisplay.TopLeft() ) );
		}
	}
	
	CDialogEx::OnMouseMove(nFlags, point);
}

BOOL CRobotSoccerProgramDlg::OnMouseWheel(UINT nFlags, short zDelta, CPoint pt)
{
	CRect rect(pt.x, pt.y, pt.x, pt.y);

	ScreenToClient(rect);

	CPoint point( rect.left, rect.top );
	m_pointMouse = point;

	if( m_propGame.IsActive() )
	{
		if( m_dcDisplay.IsPointInRect( m_pointMouse ) )
		{
			MouseCapture( m_propGame.DisplayMouseWheel( nFlags, zDelta, m_pointMouse - m_dcDisplay.TopLeft() ) );
		}
	}

	if( m_propColorSetting.IsActive() )
	{
		MouseCapture( m_propColorSetting.DisplayMouseWheel( nFlags, zDelta, m_pointMouse - m_dcDisplay.TopLeft() ) );
	}

	return CDialogEx::OnMouseWheel(nFlags, zDelta, pt);
}

void CRobotSoccerProgramDlg::SetNewCursor(unsigned int cursorID)
{
	m_nCursorID = cursorID;


	HCURSOR hCursor;
	
	if( m_nCursorID == IDC_CURSOR_ZOOM1
	 || m_nCursorID == IDC_CURSOR_ZOOM2
	 || m_nCursorID == IDC_CURSOR_PROCESSING_AREA
     || m_nCursorID == IDC_CURSOR_REVISION0 )
	{
		hCursor = AfxGetApp()->LoadCursor(m_nCursorID);
	}
	else
	{
		switch(m_nCursorID)
		{
		case (UINT)IDC_ARROW: 
			hCursor = AfxGetApp()->LoadStandardCursor(MAKEINTRESOURCE(IDC_ARROW));
			break;
		case (UINT)IDC_IBEAM: 
			hCursor = AfxGetApp()->LoadStandardCursor(MAKEINTRESOURCE(IDC_IBEAM));
			break;                        
		case (UINT)IDC_WAIT: 
			hCursor = AfxGetApp()->LoadStandardCursor(MAKEINTRESOURCE(IDC_WAIT));
			break;                         
		case (UINT)IDC_CROSS: 
			hCursor = AfxGetApp()->LoadStandardCursor(MAKEINTRESOURCE(IDC_CROSS));
			break;                        
		case (UINT)IDC_UPARROW: 
			hCursor = AfxGetApp()->LoadStandardCursor(MAKEINTRESOURCE(IDC_UPARROW));
			break;                      
		case (UINT)IDC_SIZENWSE: 
			hCursor = AfxGetApp()->LoadStandardCursor(MAKEINTRESOURCE(IDC_SIZENWSE));
			break;                     
		case (UINT)IDC_SIZENESW: 
			hCursor = AfxGetApp()->LoadStandardCursor(MAKEINTRESOURCE(IDC_SIZENESW));
			break;                     
		case (UINT)IDC_SIZEWE: 
			hCursor = AfxGetApp()->LoadStandardCursor(MAKEINTRESOURCE(IDC_SIZEWE));
			break;                       
		case (UINT)IDC_SIZENS: 
			hCursor = AfxGetApp()->LoadStandardCursor(MAKEINTRESOURCE(IDC_SIZENS));
			break;                       
		case (UINT)IDC_SIZEALL: 
			hCursor = AfxGetApp()->LoadStandardCursor(MAKEINTRESOURCE(IDC_SIZEALL));
			break;                      
		case (UINT)IDC_NO: 
			hCursor = AfxGetApp()->LoadStandardCursor(MAKEINTRESOURCE(IDC_NO));
			break;                           
		case (UINT)IDC_APPSTARTING: 
			hCursor = AfxGetApp()->LoadStandardCursor(MAKEINTRESOURCE(IDC_APPSTARTING));
			break;                  
		case (UINT)IDC_HELP: 
			hCursor = AfxGetApp()->LoadStandardCursor(MAKEINTRESOURCE(IDC_HELP));
			break;                         
		}
	}

	SetCursor(hCursor);
}

void CRobotSoccerProgramDlg::OnDestroy()
{
	m_ComPort.ClosePort();

	if( m_pLookUpTable != NULL )
		delete m_pLookUpTable;

	CDialogEx::OnDestroy();
}

void CRobotSoccerProgramDlg::OnBnClickedCancel()
{
	if( m_ImageGrabber.IsReady() )
	{
		if( m_bExit == true )
			return;
		
		if( AfxMessageBox(_T("Are you sure you want to exit the program?"), MB_YESNO|MB_ICONQUESTION) == IDYES )
		{
			m_bExit = true;
		}
	}
	else
	{
		CDialogEx::OnCancel();
	}
}

void CRobotSoccerProgramDlg::OnSelchangeComboCameraId()
{
	UpdateData(TRUE);

	m_cameraID = m_comboCameraID.GetCurSel();
	
	m_bChangeCamera = true;
}

void CRobotSoccerProgramDlg::OnBnClickedCheckRobotAll()
{
	UpdateData(TRUE);
}


void CRobotSoccerProgramDlg::OnBnClickedCheckRobot1()
{
	UpdateData(TRUE);
}


void CRobotSoccerProgramDlg::OnBnClickedCheckRobot2()
{
	UpdateData(TRUE);
}


void CRobotSoccerProgramDlg::OnBnClickedCheckRobot3()
{
	UpdateData(TRUE);
}


void CRobotSoccerProgramDlg::OnBnClickedCheckSimulation()
{
	UpdateData(TRUE);
}

void CRobotSoccerProgramDlg::OnBnClickedCheckTransmitterTest()
{
	UpdateData(TRUE);
}

void CRobotSoccerProgramDlg::InitializeSerialPort(int port)
{
	// if( port == 1 ) COM1

	m_ComPort.ClosePort();

	if( ROBOT_SOCCER_PROGRAM_COMMUNICATION_PROTOCOL == ROBOT_SOCCER_PROGRAM_COMMUNICATION_PROTOCOL_RIT )
	{
		m_ComPort.hCommWnd = this->m_hWnd;

		m_ComPort.ClosePort();

		CString strPort;
		strPort.Format(_T("COM%d"), port);
		if( !m_ComPort.OpenPort( strPort, 115200, port-1, COM_PARITY_NONE, COM_STOP1 ) )
		{
			m_bTransmitter = false;
		}
		else
		{
			m_bTransmitter = true;
		}
	}

	if( ROBOT_SOCCER_PROGRAM_COMMUNICATION_PROTOCOL == ROBOT_SOCCER_PROGRAM_COMMUNICATION_PROTOCOL_KMC )
	{
		m_ComPort.hCommWnd = this->m_hWnd;

		m_ComPort.ClosePort();

		CString strPort;
		strPort.Format(_T("COM%d"), port);
		if( !m_ComPort.OpenPort( strPort, 115200, port-1, COM_PARITY_NONE, COM_STOP1 ) )
		{
			m_bTransmitter = false;
		}
		else
		{
			m_bTransmitter = true;
		}
	}

	if( ROBOT_SOCCER_PROGRAM_COMMUNICATION_PROTOCOL == ROBOT_SOCCER_PROGRAM_COMMUNICATION_PROTOCOL_MINIROBOT )
	{
		m_ComPort.hCommWnd = this->m_hWnd;

		m_ComPort.ClosePort();

		CString strPort;
		strPort.Format(_T("COM%d"), port);
		if( !m_ComPort.OpenPort( strPort, 4800, port-1, COM_PARITY_NONE, COM_STOP1 ) )
		{
			m_bTransmitter = false;
		}
		else
		{
			m_bTransmitter = true;
		}
	}
}


void CRobotSoccerProgramDlg::OnBnClickedOpenRobotsoccerSystem()
{
	TCHAR szFilter[] = _T("Robot Soccer System File(*.RSS)|*.RSS||");
	CFileDialog dlg(TRUE, _T("default"), _T("default"), NULL, szFilter);

	if( dlg.DoModal() == IDOK )
	{
		BeginWaitCursor();

		CFile file;
		
		if( file.Open( dlg.GetPathName(), CFile::modeRead ) == FALSE )
		{
			AfxMessageBox(_T("Open File Failed. (CRobotSoccerDlg::SaveSystem)"));
			return;
		}
		
		CArchive ar( &file, CArchive::load );
		
		CString fullname = dlg.GetPathName();
		CString filename = dlg.GetFileName();
		CString path = fullname.Left( fullname.GetLength() - filename.GetLength() );
		CString filename_only = filename.Left( filename.GetLength()-4 );

		CString tmp;
		ar >> tmp;
	
		//if( tmp == ROBOT_SOCCER_VERSION )
		{
			while(1)
			{
				ar >> tmp;

				if( tmp == _T("COM Port") )
				{
					int comPort;
					ar >> comPort;
					m_comboCOMPort.SetCurSel( comPort-1 );
					UpdateData(TRUE);
					InitializeSerialPort( comPort );
				}

				if( tmp == _T("CameraID") )
				{
					int cameraID;
					ar >> cameraID;
					m_comboCameraID.SetCurSel( cameraID-1 );

					UpdateData(TRUE);

					m_cameraID = m_comboCameraID.GetCurSel();
	
					m_bChangeCamera = true;
				}


				if( tmp == _T("-END-") )
				{
					break;
				}

				if( tmp == _T("Prop:Color") )
				{
					m_propColorSetting.OpenFile( path + filename_only + _T(".PCH") );
				}

				if( tmp == _T("Prop:Vision") )
				{
					m_propVision.OpenFile( path + filename_only + _T(".VS") );
				}

				if( tmp == _T("Game:Autoposition") )
				{
					m_propGame.OpenAutopositionFile( path + filename_only + _T(".AP") );
				}

				if( tmp == _T("Game:Parameters") )
				{
					//m_propGame.OpenFile_Parameters( path + filename_only + _T(".SST") );
				}

				if( tmp == _T("Robot Enable") )
				{
					ar >> m_bManualRobotAll;
					ar >> m_bManualRobot1;
					ar >> m_bManualRobot2;
					ar >> m_bManualRobot3;
					ar >> m_bManualRobot4;
					ar >> m_bManualRobot5;
					ar >> m_bManualRobot6;
					ar >> m_bManualRobot7;
					ar >> m_bManualRobot8;
					ar >> m_bManualRobot9;
					ar >> m_bManualRobot10;
					ar >> m_bManualRobot11;
				}

				if( tmp == _T("Ball Enable") )
				{
					ar >> m_bBallSimulation;
				}
			}
		}
		
		ar.Close();
		file.Close();
		
		m_strProjectName = dlg.GetPathName();

		UpdateData(FALSE);

		EndWaitCursor();
	}
}

void CRobotSoccerProgramDlg::OnBnClickedSaveRobotsoccerSystem()
{
	TCHAR szFilter[] = _T("Robot Soccer System File(*.RSS)|*.RSS||");
	CFileDialog dlg(FALSE, _T("default"), _T("default"), NULL, szFilter);
		
	if( dlg.DoModal() == IDOK )
	{
		BeginWaitCursor();

		CFile file;
		
		file.Open( dlg.GetPathName(), CFile::modeCreate | CFile::modeWrite );
		CArchive ar( &file, CArchive::store );
		
		CString tmp= ROBOT_SOCCER_VERSION;
		ar << tmp;
		
		tmp = _T("COM Port");
		ar << tmp;
		ar << (m_comboCOMPort.GetCurSel()+1);

		tmp = _T("CameraID");
		ar << tmp;
		ar << (m_comboCameraID.GetCurSel()+1);

		tmp = _T("Robot Enable");
		ar << tmp;
		ar << m_bManualRobotAll;
		ar << m_bManualRobot1;
		ar << m_bManualRobot2;
		ar << m_bManualRobot3;
		ar << m_bManualRobot4;
		ar << m_bManualRobot5;
		ar << m_bManualRobot6;
		ar << m_bManualRobot7;
		ar << m_bManualRobot8;
		ar << m_bManualRobot9;
		ar << m_bManualRobot10;
		ar << m_bManualRobot11;

		tmp = _T("Ball Enable");
		ar << tmp;
		ar << m_bBallSimulation;

		CString fullname = dlg.GetPathName();
		CString filename = dlg.GetFileName();
		CString path = fullname.Left( fullname.GetLength() - filename.GetLength() );
		CString filename_only = filename.Left( filename.GetLength()-4 );

		tmp = _T("Prop:Color");
		ar << tmp;
		m_propColorSetting.SaveFile( path + filename_only + _T(".PCH") );

		tmp = _T("Prop:Vision");
		ar << tmp;
		m_propVision.SaveFile( path + filename_only + _T(".VS") );

		tmp = _T("Game:Autoposition");
		ar << tmp;
		m_propGame.SaveAutopositionFile( path + filename_only + _T(".AP") );

		tmp = _T("Game:Parameters");
		ar << tmp;
		m_propGame.SaveFile_Parameters( path + filename_only + _T(".SST") );

		tmp = _T("-END-");
		ar << tmp;

		ar.Close();
		file.Close();

		UpdateData(TRUE);

		m_strProjectName = dlg.GetPathName();

		UpdateData(FALSE);

		EndWaitCursor();	
	}
}

void CRobotSoccerProgramDlg::OnBnClickedCheckRobot4()
{
	UpdateData(TRUE);
}


void CRobotSoccerProgramDlg::OnBnClickedCheckRobot5()
{
	UpdateData(TRUE);
}


void CRobotSoccerProgramDlg::OnBnClickedCheckRobot6()
{
	UpdateData(TRUE);
}


void CRobotSoccerProgramDlg::OnBnClickedCheckRobot7()
{
	UpdateData(TRUE);
}


void CRobotSoccerProgramDlg::OnBnClickedCheckRobot8()
{
	UpdateData(TRUE);
}


void CRobotSoccerProgramDlg::OnBnClickedCheckRobot9()
{
	UpdateData(TRUE);
}


void CRobotSoccerProgramDlg::OnBnClickedCheckRobot12()
{
	UpdateData(TRUE);
}


void CRobotSoccerProgramDlg::OnBnClickedCheckRobot13()
{
	UpdateData(TRUE);
}

void CRobotSoccerProgramDlg::OnSelchangeComboComPort()
{
	UpdateData(TRUE);

	int i = m_comboCOMPort.GetCurSel() + 1;

	InitializeSerialPort( i );
}

void CRobotSoccerProgramDlg::OnBnClickedCheckBall()
{
	UpdateData(TRUE);
}

BOOL CRobotSoccerProgramDlg::OnSetCursor(CWnd* pWnd, UINT nHitTest, UINT message)
{
	return FALSE;

	//return CDialogEx::OnSetCursor(pWnd, nHitTest, message);
}

void CRobotSoccerProgramDlg::OnBnClickedCheckRobotInfoSave()
{
	UpdateData(TRUE);

	if( m_bRobotInfoSave )
	{
		if( m_fpRobotInfo != NULL )
		{
			fclose( m_fpRobotInfo  );
		}

		m_fpRobotInfo = fopen("test.txt", "wt");
	}
	else
	{
		fclose( m_fpRobotInfo  );
		m_fpRobotInfo = NULL;
	}
}

void CRobotSoccerProgramDlg::OnSelchangeComboRobotTestBehavior()
{
	UpdateData(TRUE);

	m_RobotTestBehavior = m_comboRobotTestBehavior.GetCurSel();
}


void CRobotSoccerProgramDlg::OnBnClickedCheckCameraImageOnGame()
{
	UpdateData(TRUE);
}

//kinda like process method for swing worker which will run in the main window thread
LRESULT CRobotSoccerProgramDlg::OnThreadMessage(WPARAM wParam, LPARAM lParam)
{
	UpdateData(true);
	CEdit* Display;
	CListBox* bigBox;
	number++;
	if (number >9) {
		number =0;
	}
	
	
	CObjectVelocityInfo object = *(reinterpret_cast<CObjectVelocityInfo*>(wParam));
	std::vector<std::string> y = *(reinterpret_cast<std::vector<std::string>*>(lParam));
	// debugging output start
	bigBox = reinterpret_cast<CListBox *>(GetDlgItem(IDC_DEBUG_BIG_BOX));
	bigBox->ResetContent();

	for (size_t i=0; i<y.size(); i++) {
		std::wstring ws;
		ws.assign(y.at(i).begin(),y.at(i).end());
		bigBox->InsertString(-1, ws.c_str());
	}

    //bigBox->InsertString(-1,_T("dasdf"));
	Display = reinterpret_cast<CEdit *>(GetDlgItem(IDC_DEBUG_BOX));
	
	//std::string timestamp = *(reinterpret_cast<std::string*>(lParam));
	std::wstringstream ss;
	ss <<  object.m_LinearVelocity[0];
	ss << " ";
	ss << number;
	ss << " ";

	//std::wstring ws;
	//ws.assign(timestamp.begin(),timestamp.end());

	//ss << ws;

	//Display->SetWindowText(ss.str().c_str()); 
	//Display->SetWindowText(ws.c_str());

	//debuggin output end
	m_cs.Lock();
	for( int i=0 ; i<11 ; i++ ){

		javaVelocityInfo.m_LinearVelocity[i] = object.m_LinearVelocity[i];
		javaVelocityInfo.m_AngularVelocity[i] = object.m_AngularVelocity[i];
	}
	m_cs.Unlock();
	return 0;
	
}

//the function that the thread will be running
UINT DataReceivingThread(void *pParam)
{
	CRobotSoccerProgramDlg* pThis= (CRobotSoccerProgramDlg*)pParam;
	
	bool listening = true;
	std::string outputResult;
	std::string timestamp;
	
	std::string message;
	std::vector<std::string> x;
	CObjectVelocityInfo velocityObject;

	bool found = false;
	char buffer[512];
	int len = 512;

	std::string stringResult;

	double doubleResult;

	while ( listening ) {
		
		int iResult = recv(pThis->getSocket(), buffer,  len, 0);
		if (iResult > 0) {
				std::string hello(buffer, 512);
				outputResult = hello;
				x.clear();
				std::vector<std::string> elems;
				std::stringstream ss(outputResult);
				std::string item;
				while (std::getline(ss, item, '\r')) {
					elems.push_back(item);
				}
				x = elems;
				

				for (size_t index=0; index<x.size(); index++) {
					std::vector<char> writable(x.at(index).begin(), x.at(index).end());
					writable.push_back('\0');
					size_t pos = 0;
					if (x.at(index).length() < 14) {
					}
					else if ( pos = x.at(index).find("lin bot0:" ) != std::string::npos) {
						stringResult = x.at(index).substr(pos+10);
						doubleResult = stod(stringResult);
						velocityObject.m_LinearVelocity[0] = stod(x.at(index).substr(pos+10));

						std::ostringstream strs;
						strs << velocityObject.m_LinearVelocity[0];
						pThis->SendMessage(WM_MY_THREAD_MESSAGE,reinterpret_cast<WPARAM>(&velocityObject), reinterpret_cast<LPARAM>(&elems));
					}
					else if ( pos = x.at(index).find("lin bot1:" ) != std::string::npos ) {

						velocityObject.m_LinearVelocity[1] = stod(x.at(index).substr(pos+10));
					}
					else if (pos = x.at(index).find("lin bot2:" ) != std::string::npos ) {

						velocityObject.m_LinearVelocity[2] = stod(x.at(index).substr(pos+10));
					}
					else if ( pos = x.at(index).find("lin bot3:" ) != std::string::npos) {

						velocityObject.m_LinearVelocity[3] = stod(x.at(index).substr(pos+10));
					}
					else if ( pos = x.at(index).find("lin bot4:" ) != std::string::npos) {

						velocityObject.m_LinearVelocity[4] = stod(x.at(index).substr(pos+10));
					}
					else if ( pos = x.at(index).find("ang bot0:" ) != std::string::npos ) {
						velocityObject.m_AngularVelocity[0] = stod(x.at(index).substr(pos+10));
					}
					else if (pos = x.at(index).find("ang bot1:" ) != std::string::npos ) {
						velocityObject.m_AngularVelocity[1] = stod(x.at(index).substr(pos+10));
					}
					else if ( pos = x.at(index).find("ang bot2:" ) != std::string::npos) {
						velocityObject.m_AngularVelocity[2] = stod(x.at(index).substr(pos+10));
					}
					else if ( pos = x.at(index).find("ang bot3:" ) != std::string::npos) {
						velocityObject.m_AngularVelocity[3] = stod(x.at(index).substr(pos+10));
					}
					else if ( pos = x.at(index).find("ang bot4:" ) != std::string::npos) {
						velocityObject.m_AngularVelocity[4] = stod(x.at(index).substr(pos+10));;
					}
					else if ( pos = x.at(index).find("Timestamp" ) != std::string::npos) {
						timestamp = x.at(index);
					}
					
				}
				
		} 
	}
	
    return 0;
}