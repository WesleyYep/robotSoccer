#include "StdAfx.h"
#include "GameGUI.h"


#define _USE_MATH_DEFINES
#include <math.h>


CGameGUI::CGameGUI(void)
{
	InitializeCriticalSection(&m_critGameGUI);

	m_robotN    = ROBOT_SOCCER_RROGRAM_ROBOT_N;
	m_opponentN = ROBOT_SOCCER_RROGRAM_ROBOT_N;


	m_Robot_share = new CRobot[m_robotN];
	m_Robot_draw  = new CRobot[m_robotN];



	m_Robot = new CRobot[m_robotN];
	m_Opponent = new CBall[m_opponentN];

	for( int i=0 ; i<m_robotN ; i++ )
	{
		m_Robot[i].SetID( i+1 );
		//robot starting position on simulation
		//m_Robot[i].SetPositionCurrent( CPointDouble( -0.25, m_ground.Height() - (0.05 + i*0.15) ) );
		m_Robot[i].SetPositionCurrent( CPointDouble( 0.1, m_ground.Height() - (0.10 + i*0.15) ) );
		m_Robot[i].SetOrientationCurrent( 0 );
	}

	for( int i=0 ; i<m_opponentN ; i++ )
	{
		m_Opponent[i].SetPositionCurrent( CPointDouble( m_ground.Width() + 0.25, 0.05 + i*0.15 ) );
//		m_Opponent[i].SetPosition( CPointDouble( -0.08, i*0.1+1.5 ) );
		m_Opponent[i].SetRadius( 0.0375 );
	}

//	m_Ball.SetPositionPast( CPointDouble( 0.05, 0.05 ) );
	m_Ball.SetPositionCurrent( CPointDouble( 0.05, 0.05 ) );
//	m_Ball.SetPosition( CPointDouble( 0.05, 0.05 ) );\

	m_timeGame = 0;
	m_timeStart = 0;
	m_timeNow = 0;
}


CGameGUI::~CGameGUI(void)
{
	delete[] m_Robot;
	delete[] m_Robot_draw;
	delete[] m_Robot_share;
	delete[] m_Opponent;

	DeleteCriticalSection(&m_critGameGUI);
}

void CGameGUI::DrawDisplay(CFrameDC *pDC, bool bRobotMessage, bool bRobotMessageOnly)
{
	m_ground.Draw( pDC, RGB(255,255,255), true, false );

	for( int i=0 ; i<m_robotN ; i++ )
	{
		if( ! m_Robot_draw[i].IsExist() )
		{
			m_Robot_draw[i].SetPositionCurrent( CPointDouble( -0.25, m_ground.Height() - (0.05 + i*0.15)) );
			m_Robot_draw[i].SetPosition( CPointDouble( -0.25, m_ground.Height() - (0.05 + i*0.15) ) );
		}

		CRobotDraw robot;
		robot.DrawBody( pDC, &m_ground, &m_Robot_draw[i] );
		robot.DrawBodyCurrent( pDC, &m_ground, &m_Robot_draw[i] );

		if( m_Robot_draw[i].IsExist() )
		{	
			if( bRobotMessage || bRobotMessageOnly && m_Robot_draw[i].IsSelect() )
				robot.DrawInfoToGround( pDC, &m_ground, &m_Robot_draw[i] );
 			
 			if( m_Robot_draw[i].IsSelect()  )
 				robot.DrawMission( pDC, &m_ground, &m_Robot_draw[i] );
 			
 			//if( m_Robot_draw[i].IsSelect() )
 			//	robot.DrawPath( pDC );
		}
	}

	for( int i=0 ; i<m_opponentN ; i++ )
	{
		CBallDraw opp;

		opp.SetColor( 0, 3, RGB(64,92,255), RGB(64,255,255) );
		opp.SetColor( 0, 0, RGB(128,128,128), RGB(0,0,0) );
		opp.SetColor( 0, 1, RGB(64,92,255), RGB(0,0,0) );
		opp.SetColor( 0, 2, RGB(32,46,128), RGB(0,0,0) );

		opp.SetColor( 1, 3, RGB(64,92,255), RGB(64,255,255) );
		opp.SetColor( 1, 0, RGB(128,128,128), RGB(0,0,0) );
		opp.SetColor( 1, 1, RGB(64,92,255), RGB(0,0,0) );
		opp.SetColor( 1, 2, RGB(32,46,128), RGB(0,0,0) );

		opp.Draw( pDC, &m_ground, &m_Opponent[i] );
		opp.DrawCurrent( pDC, &m_ground, &m_Opponent[i] );
	}


	{
		CBallDraw ball;

		ball.SetColor( 0, 3, RGB(255,0,0), RGB(0,255,0) );
		ball.SetColor( 0, 0, RGB(128,128,128), RGB(0,0,0) );
		ball.SetColor( 0, 1, RGB(255,0,0), RGB(0,0,0) );
		ball.SetColor( 0, 2, RGB(128,0,0), RGB(0,0,0) );
	
		ball.SetColor( 1, 3, RGB(128,0,0), RGB(0,255,0) );
		ball.SetColor( 1, 0, RGB(128,128,128), RGB(0,0,0) );
		ball.SetColor( 1, 1, RGB(255,0,0), RGB(0,0,0) );
		ball.SetColor( 1, 2, RGB(128,0,0), RGB(0,0,0) );

		ball.Draw( pDC, &m_ground, &m_Ball  );
		ball.DrawCurrent( pDC, &m_ground, &m_Ball );
	}

	{
		CRect text_rect(520, 5, 630, 20);

		CString tmp;
		tmp.Format(_T("%.1lf sec"), m_timeGame);

		pDC->SetBkMode( TRANSPARENT );
		pDC->SetTextColor( RGB( 255,255,255 ) );
		pDC->DrawText(_T("Game Time : "), text_rect, DT_LEFT);
		pDC->DrawText( tmp, text_rect, DT_RIGHT);
	}


	{
		for( int i=0 ; i<m_robotN ; i++ )
		{
			int rect_width = 130;
			int rect_x = 105 + i*(rect_width+20);
			int rect_y = 390;
			int line_y = 15;

			if( ROBOT_SOCCER_RROGRAM_ROBOT_N == ROBOT_SOCCER_RROGRAM_ROBOT_5 )
			{
				rect_width = 115;
				rect_x = 15 + i*(rect_width+10);
			}

			CRect rectInfo_Frame    ( rect_x             ,    rect_y         , rect_x+rect_width  , rect_y+line_y*5);
			CRect rectInfo_ID_Name  ( rect_x             ,    rect_y         , rect_x+rect_width  , rect_y+line_y*1);
			CRect rectInfo_Position ( rect_x             ,    rect_y+line_y*1, rect_x+rect_width  , rect_y+line_y*2);
			CRect rectInfo_Velocity1( rect_x             ,    rect_y+line_y*2, rect_x+rect_width/2, rect_y+line_y*3);
			CRect rectInfo_Velocity2( rect_x+rect_width/2,    rect_y+line_y*2, rect_x+rect_width  , rect_y+line_y*3);
			CRect rectInfo_Role     ( rect_x             ,    rect_y+line_y*3, rect_x+rect_width  , rect_y+line_y*4);
			CRect rectInfo_Mission  ( rect_x             ,    rect_y+line_y*4, rect_x+rect_width  , rect_y+line_y*5);

			{
				rectInfo_Frame.InflateRect(4,4,4,4);
				CBrush brush, *pOldBrush;
				brush.CreateSolidBrush( RGB(30,30,30) );


				pOldBrush = (CBrush*)pDC->SelectObject( &brush );
				
				CPen pen, *pOldPen;

				COLORREF colorPen;
				if( m_Robot_draw[i].IsSelect() )
					colorPen = RGB(128,255,128);
				else
					colorPen = RGB(128,128,128);

				pen.CreatePen( PS_SOLID, 2, colorPen );
				pOldPen = (CPen*)pDC->SelectObject( &pen );
				
				pDC->Rectangle(rectInfo_Frame);
				
				pDC->SelectObject( pOldPen );
				pDC->SelectObject( pOldBrush );
			}

			pDC->SetBkMode( TRANSPARENT );
			pDC->SetTextColor( RGB( 255,255,255 ) );

			int missionN = m_Robot_draw[i].GetMissionN();
			CMission missionRecent = m_Robot_draw[i].GetRecentMission();

			int missionRecentID = m_Robot_draw[i].m_MissionSet.GetRecentMissionID()+1;


			CString tmp;
			tmp.Format(_T("[%d] %s"),
				m_Robot_draw[i].ID(),
				m_Robot_draw[i].Name() );
			pDC->DrawText( tmp, rectInfo_ID_Name, DT_LEFT );

			tmp.Format(_T("(%d/%d)"), missionRecentID, missionN );
			pDC->DrawText( tmp, rectInfo_ID_Name, DT_RIGHT );

			tmp.Format(_T("( %.2lf, %.2lf : %.1lf )"),
				m_Robot_draw[i].Position().x,
				m_Robot_draw[i].Position().y,
				m_Robot_draw[i].OrientationDegree());
			pDC->DrawText( tmp, rectInfo_Position, DT_CENTER );

			tmp.Format(_T("%.2lf m/s"), m_Robot_draw[i].VelocityLinear() );
			pDC->DrawText( tmp, rectInfo_Velocity1, DT_RIGHT );

			tmp.Format(_T("%.2lf °/s"), m_Robot_draw[i].VelocityAngularDegree() );
			pDC->DrawText( tmp, rectInfo_Velocity2, DT_RIGHT );

			tmp.Format(_T("Role:%s"), m_Robot_draw[i].Role() );
			pDC->DrawText( tmp, rectInfo_Role, DT_LEFT );


			tmp.Format(_T("Action:%s"), missionRecent.text );
			if( missionRecent.type == MissionType::None )
			{
				pDC->SetTextColor( RGB( 64, 64, 64 ) );
			}
			else
			{
				pDC->SetTextColor( RGB( 255,255,255 ) );
			}
			pDC->DrawText( tmp, rectInfo_Mission, DT_LEFT );
			
		}
	}

	{
		CRect text_rect(20, 25, 220, 40);

		CString tmp;
		tmp.Format(_T(" Game Status: %s"), m_situationCurrent.name );

		pDC->SetBkMode( TRANSPARENT );
		pDC->SetTextColor( RGB( 255,255,255 ) );
		pDC->DrawText( tmp, text_rect, DT_LEFT);
	}
}

void CGameGUI::DrawAutoPosition( CFrameDC *pDC, CString situation )
{
	CAutopositionData::Team auto_pos;
	auto_pos= m_AutoPositionData.GetAutoPositionData( situation );

	if( auto_pos.bValid == false )
	{
	}
	else
	{
		for( int i=0 ; i<(int)auto_pos.robot.size() ; i++ )
		{
			CRobot robot;
			robot.SetPosition( CPointDouble( auto_pos.robot[i].x, auto_pos.robot[i].y + 10 ) );
			robot.SetPositionCurrent( CPointDouble( auto_pos.robot[i].x, auto_pos.robot[i].y ) );
			robot.SetOrientationDegree( auto_pos.robot[i].orientation );

			CRobotDraw robotDraw;
			robotDraw.DrawBody( pDC, &m_ground, &robot, 0.666 );
			robotDraw.DrawBodyCurrent( pDC, &m_ground, &robot );
			robotDraw.DrawInfoToGround( pDC, &m_ground, &robot, auto_pos.robot[i].name );
		}
	}
}


void CGameGUI::DrawStrategyParameter( CDC *pDC )
{
	{	// Blue Line
		CPen pen, *pOldPen;
		pen.CreatePen( PS_SOLID, 1, RGB(0,0,255) );
		pOldPen = (CPen*)pDC->SelectObject( &pen );

		{
			// Avoid Bound
			CPointDouble p1 = CPointDouble(m_StrategyParam.m_AvoidBoundSize, m_StrategyParam.m_AvoidBoundSize ) * m_ground.ScreenRatio() + m_ground.Origin();
			CPointDouble p2 = CPointDouble(m_ground.Width()-m_StrategyParam.m_AvoidBoundSize, m_ground.Height()-m_StrategyParam.m_AvoidBoundSize ) * m_ground.ScreenRatio() + m_ground.Origin();
			pDC->Rectangle( (int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y );
		}

		pDC->SelectObject( pOldPen );
	}

	{	// Green Line
		CPen pen, *pOldPen;
		pen.CreatePen( PS_SOLID, 1, RGB(0,255,0) );
		pOldPen = (CPen*)pDC->SelectObject( &pen );

		{
			// Goalkeeper Clear Area
			CPointDouble p1 = CPointDouble(0                         , m_ground.Height()/2 - m_StrategyParam.m_GoalkeeperClearYSize) * m_ground.ScreenRatio() + m_ground.Origin();
			CPointDouble p2 = CPointDouble(m_StrategyParam.m_GoalkeeperClearX, m_ground.Height()/2 + m_StrategyParam.m_GoalkeeperClearYSize) * m_ground.ScreenRatio() + m_ground.Origin();
			pDC->Rectangle( (int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y );
		}

		pDC->SelectObject( pOldPen );
	}	
	
	{	// Red Line
		CPen pen, *pOldPen;
		pen.CreatePen( PS_SOLID, 1, RGB(255,0,0) );
		pOldPen = (CPen*)pDC->SelectObject( &pen );

		{
			// Goal Y Size
			CPointDouble p1 = CPointDouble(0, m_ground.Height()/2 - m_StrategyParam.m_GoalYSize) * m_ground.ScreenRatio() + m_ground.Origin();
			CPointDouble p2 = CPointDouble(0, m_ground.Height()/2 + m_StrategyParam.m_GoalYSize) * m_ground.ScreenRatio() + m_ground.Origin();
			pDC->MoveTo( p1.Integer() );
			pDC->LineTo( p2.Integer() );
		}

		{
			// DefenseLineX
			CPointDouble p1 = CPointDouble(m_StrategyParam.m_DefenseLineX, 0                       ) * m_ground.ScreenRatio() + m_ground.Origin();
			CPointDouble p2 = CPointDouble(m_StrategyParam.m_DefenseLineX, m_ground.Height() ) * m_ground.ScreenRatio() + m_ground.Origin();
			pDC->MoveTo( p1.Integer() );
			pDC->LineTo( p2.Integer() );
		}
		{
			// OffenseLineX
			CPointDouble p1 = CPointDouble(m_StrategyParam.m_OffenseLineX, 0                       ) * m_ground.ScreenRatio() + m_ground.Origin();
			CPointDouble p2 = CPointDouble(m_StrategyParam.m_OffenseLineX, m_ground.Height() ) * m_ground.ScreenRatio() + m_ground.Origin();
			pDC->MoveTo( p1.Integer() );
			pDC->LineTo( p2.Integer() );
		}
		pDC->SelectObject( pOldPen );
	}

	int size = 2;
	{	// Yellow Line
		CPen pen, *pOldPen;
		pen.CreatePen( PS_SOLID, 1, RGB(255,255,0) );
		pOldPen = (CPen*)pDC->SelectObject( &pen );

		{
			// Goalkeeper Position
			CPointDouble p1 = CPointDouble(m_StrategyParam.m_GoalkeeperX, m_ground.Height()/2 - m_StrategyParam.m_GoalkeeperYSize) * m_ground.ScreenRatio() + m_ground.Origin();
			CPointDouble p2 = CPointDouble(m_StrategyParam.m_GoalkeeperX, m_ground.Height()/2 + m_StrategyParam.m_GoalkeeperYSize) * m_ground.ScreenRatio() + m_ground.Origin();
			pDC->MoveTo( p1.Integer() );
			pDC->LineTo( p2.Integer() );
		}
		{
			// Sweeper Position
			CPointDouble p1 = CPointDouble(m_StrategyParam.m_SweeperX, m_ground.Height()/2 - m_StrategyParam.m_SweeperYSize) * m_ground.ScreenRatio() + m_ground.Origin();
			CPointDouble p2 = CPointDouble(m_StrategyParam.m_SweeperX, m_ground.Height()/2 + m_StrategyParam.m_SweeperYSize) * m_ground.ScreenRatio() + m_ground.Origin();
			pDC->MoveTo( p1.Integer() );
			pDC->LineTo( p2.Integer() );
		}

		{
			// Left  : Wing Back & Wing Forward
			CPointDouble wb_def = CPointDouble(m_StrategyParam.m_WingBack_Def_X, m_ground.Height()/2-m_StrategyParam.m_WingBack_Def_Y ) * m_ground.ScreenRatio() + m_ground.Origin();
			CPointDouble wb_nor = CPointDouble(m_StrategyParam.m_WingBack_Nor_X, m_ground.Height()/2-m_StrategyParam.m_WingBack_Nor_Y ) * m_ground.ScreenRatio() + m_ground.Origin();
			CPointDouble wf_nor = CPointDouble(m_StrategyParam.m_WingForward_Nor_X, m_ground.Height()/2-m_StrategyParam.m_WingForward_Nor_Y ) * m_ground.ScreenRatio() + m_ground.Origin();
			CPointDouble wf_off = CPointDouble(m_StrategyParam.m_WingForward_Off_X, m_ground.Height()/2-m_StrategyParam.m_WingForward_Off_Y ) * m_ground.ScreenRatio() + m_ground.Origin();

			pDC->MoveTo( wb_def.Integer() );
			pDC->LineTo( wb_nor.Integer() );
			pDC->MoveTo( wf_nor.Integer() );
			pDC->LineTo( wf_off.Integer() );

			pDC->Ellipse( (int)wb_def.x - size, (int)wb_def.y - size, (int)wb_def.x + size, (int)wb_def.y + size );
			pDC->Ellipse( (int)wb_nor.x - size, (int)wb_nor.y - size, (int)wb_nor.x + size, (int)wb_nor.y + size );
			pDC->Ellipse( (int)wf_nor.x - size, (int)wf_nor.y - size, (int)wf_nor.x + size, (int)wf_nor.y + size );
			pDC->Ellipse( (int)wf_off.x - size, (int)wf_off.y - size, (int)wf_off.x + size, (int)wf_off.y + size );
		}
		{
			// Right : Wing Back & Wing Forward
			CPointDouble wb_def = CPointDouble(m_StrategyParam.m_WingBack_Def_X, m_ground.Height()/2+m_StrategyParam.m_WingBack_Def_Y ) * m_ground.ScreenRatio() + m_ground.Origin();
			CPointDouble wb_nor = CPointDouble(m_StrategyParam.m_WingBack_Nor_X, m_ground.Height()/2+m_StrategyParam.m_WingBack_Nor_Y ) * m_ground.ScreenRatio() + m_ground.Origin();
			CPointDouble wf_nor = CPointDouble(m_StrategyParam.m_WingForward_Nor_X, m_ground.Height()/2+m_StrategyParam.m_WingForward_Nor_Y ) * m_ground.ScreenRatio() + m_ground.Origin();
			CPointDouble wf_off = CPointDouble(m_StrategyParam.m_WingForward_Off_X, m_ground.Height()/2+m_StrategyParam.m_WingForward_Off_Y ) * m_ground.ScreenRatio() + m_ground.Origin();

			pDC->MoveTo( wb_def.Integer() );
			pDC->LineTo( wb_nor.Integer() );
			pDC->MoveTo( wf_nor.Integer() );
			pDC->LineTo( wf_off.Integer() );

			pDC->Ellipse( (int)wb_def.x - size, (int)wb_def.y - size, (int)wb_def.x + size, (int)wb_def.y + size );
			pDC->Ellipse( (int)wb_nor.x - size, (int)wb_nor.y - size, (int)wb_nor.x + size, (int)wb_nor.y + size );
			pDC->Ellipse( (int)wf_nor.x - size, (int)wf_nor.y - size, (int)wf_nor.x + size, (int)wf_nor.y + size );
			pDC->Ellipse( (int)wf_off.x - size, (int)wf_off.y - size, (int)wf_off.x + size, (int)wf_off.y + size );
		}
		{
			// Center : Center Back & Center Forward
			CPointDouble cb_def = CPointDouble(m_StrategyParam.m_CenterBack_Def_X, m_ground.Height()/2 ) * m_ground.ScreenRatio() + m_ground.Origin();
			CPointDouble cb_nor = CPointDouble(m_StrategyParam.m_CenterBack_Nor_X, m_ground.Height()/2 ) * m_ground.ScreenRatio() + m_ground.Origin();
			CPointDouble cf_nor = CPointDouble(m_StrategyParam.m_CenterForward_Nor_X, m_ground.Height()/2 ) * m_ground.ScreenRatio() + m_ground.Origin();
			CPointDouble cf_off = CPointDouble(m_StrategyParam.m_CenterForward_Off_X, m_ground.Height()/2 ) * m_ground.ScreenRatio() + m_ground.Origin();

			pDC->MoveTo( cb_def.Integer() );
			pDC->LineTo( cb_nor.Integer() );
			pDC->MoveTo( cf_nor.Integer() );
			pDC->LineTo( cf_off.Integer() );

			pDC->Ellipse( (int)cb_def.x - size, (int)cb_def.y - size, (int)cb_def.x + size, (int)cb_def.y + size );
			pDC->Ellipse( (int)cb_nor.x - size, (int)cb_nor.y - size, (int)cb_nor.x + size, (int)cb_nor.y + size );
			pDC->Ellipse( (int)cf_nor.x - size, (int)cf_nor.y - size, (int)cf_nor.x + size, (int)cf_nor.y + size );
			pDC->Ellipse( (int)cf_off.x - size, (int)cf_off.y - size, (int)cf_off.x + size, (int)cf_off.y + size );

			CPointDouble cb_def1 = CPointDouble(m_StrategyParam.m_CenterBack_Def_X, m_ground.Height()/2-m_StrategyParam.m_CenterBack_Def_Y ) * m_ground.ScreenRatio() + m_ground.Origin();
			CPointDouble cb_def2 = CPointDouble(m_StrategyParam.m_CenterBack_Def_X, m_ground.Height()/2+m_StrategyParam.m_CenterBack_Def_Y ) * m_ground.ScreenRatio() + m_ground.Origin();
			pDC->MoveTo( cb_def1.Integer() );
			pDC->LineTo( cb_def2.Integer() );
		}

		{
			// MF X1
			CPointDouble p1 = CPointDouble(m_StrategyParam.m_MF_X1, 0                       ) * m_ground.ScreenRatio() + m_ground.Origin();
			CPointDouble p2 = CPointDouble(m_StrategyParam.m_MF_X1, m_ground.Height() ) * m_ground.ScreenRatio() + m_ground.Origin();
			pDC->MoveTo( p1.Integer() );
			pDC->LineTo( p2.Integer() );
		}
		{
			// MF X2
			CPointDouble p1 = CPointDouble(m_StrategyParam.m_MF_X2, 0                       ) * m_ground.ScreenRatio() + m_ground.Origin();
			CPointDouble p2 = CPointDouble(m_StrategyParam.m_MF_X2, m_ground.Height() ) * m_ground.ScreenRatio() + m_ground.Origin();
			pDC->MoveTo( p1.Integer() );
			pDC->LineTo( p2.Integer() );
		}

		pDC->SelectObject( pOldPen );
	}	
}

//////////////////////////////////////////////////////////////////////////
// Mouse
//////////////////////////////////////////////////////////////////////////

bool CGameGUI::Screen_Grasp(CPoint p)
{
	bool result = false;
	double near_size = 0.05;

	CPointDouble point(p);
	m_ground.ScreenToGround( point );


	if( m_Ball.IsExist() )
	if( m_Ball.IsClick( point, near_size ) )
	{
		if( !m_Ball.IsGrasp() )
		{
			m_Ball.Grasp();
		}

		result = true;

		return result; // 로봇과 중복 선택 방지
	}

	for( int id=0 ; id<m_opponentN ; id++ )
	{
		if( m_Opponent[id].IsExist() )
		if( m_Opponent[id].IsClick( point, near_size ) )
		{
			if( !m_Opponent[id].IsGrasp() )
			{
				m_Opponent[id].Grasp();
			}

			result = true;
		}
	}

	for( int id=0 ; id<m_robotN ; id++ )
	{
		if( m_Robot[id].IsExist() )
		if( m_Robot[id].IsClick( point, near_size ) )
		{
			if( !m_Robot[id].IsGrasp() )
			{
				m_Robot[id].Grasp();
			}
			
			result = true;
		}
	}

	return result;
}

bool CGameGUI::Screen_IsObject(CPoint p)
{
	bool result = false;
	double near_size = 0.05;

	CPointDouble point(p);
	m_ground.ScreenToGround( point );


	if( m_Ball.IsExist() )
	if( m_Ball.IsClick( point, near_size ) )
	{
		result = true;

		return result; // 로봇과 중복 선택 방지
	}

	for( int id=0 ; id<m_opponentN ; id++ )
	{
		if( m_Opponent[id].IsExist() )
		if( m_Opponent[id].IsClick( point, near_size ) )
		{
			result = true;
		}
	}

	for( int id=0 ; id<m_robotN ; id++ )
	{
		if( m_Robot[id].IsExist() )
		if( m_Robot[id].IsClick( point, near_size ) )
		{
			result = true;
		}
	}

	return result;
}

void CGameGUI::Screen_SelectObject(CPoint p1, CPoint p2)
{
	CPointDouble point1(p1);
	CPointDouble point2(p2);
	m_ground.ScreenToGround( point1 );
	m_ground.ScreenToGround( point2 );


	bool bNoObject = true;

	if( m_Ball.IsExist() )
	if( IsPointInRect( point1, point2 , m_Ball.Position() ) )
	{
		if( !m_Ball.IsSelect() )
		{
			m_Ball.Select();
		}

		bNoObject = false;
	}

	
	for( int id=0 ; id<m_opponentN ; id++ )
	{
		if( m_Opponent[id].IsExist() )
		{
			if( IsPointInRect( point1, point2, m_Opponent[id].Position() ) )
			{
				if( !m_Opponent[id].IsSelect() )
				{
					m_Opponent[id].Select();
				}

				bNoObject = false;
			}
			else
			{
				m_Opponent[id].Deselect();
			}
		}
	}

	for( int id=0 ; id<m_robotN ; id++ )
	{
		if( m_Robot[id].IsExist() )
		{
			if( IsPointInRect( point1, point2, m_Robot[id].Position() ) )
			{
				if( !m_Robot[id].IsSelect() )
				{
					m_Robot[id].Select();
				}

				bNoObject = false;
			}
			else
			{
				m_Robot[id].Deselect();
			}
		}
	}

	if( bNoObject )
	{
		m_Ball.Deselect();

		for( int id=0 ; id<m_opponentN ; id++ )
		{
			if( m_Opponent[id].IsExist() )
			{
				m_Opponent[id].Deselect();
			}
		}
			
		for( int id=0 ; id<m_robotN ; id++ )
		{
			if( m_Robot[id].IsExist() )
			{
				m_Robot[id].Deselect();
			}
		}
	}
}

void CGameGUI::Screen_DragObject(CPoint p1, CPoint p2)
{
	CPointDouble point1(p1);
	CPointDouble point2(p2);
	m_ground.ScreenToGround( point1 );
	m_ground.ScreenToGround( point2 );

		if( m_Ball.IsExist() )
		if( m_Ball.IsGrasp() )
			m_Ball.Drag( point2-point1 );

	for( int id=0 ; id<m_opponentN ; id++ )
	{
		if( m_Opponent[id].IsExist() )
		if( m_Opponent[id].IsGrasp() )
			m_Opponent[id].Drag( point2-point1 );
	}

	for( int id=0 ; id<m_robotN ; id++ )
	{
		if( m_Robot[id].IsExist() )
		if( m_Robot[id].IsGrasp() )
			m_Robot[id].Drag( point2-point1 );
	}
}

void CGameGUI::Screen_DropObject(CPoint p1, CPoint p2, bool bCtrl )
{
	if( p1 != p2 )
	{
		bCtrl = true;
	}

	CPointDouble point1(p1);
	CPointDouble point2(p2);
	m_ground.ScreenToGround( point1 );
	m_ground.ScreenToGround( point2 );


		if( m_Ball.IsExist() )
		if( m_Ball.IsGrasp() )
		{
			m_Ball.Drop( point2-point1 );
			
			if( p1 == p2 && IsPointInRect( point1, point2, m_Ball.Position() ) )
			{
				if( m_Ball.IsSelect() )
					m_Ball.Deselect();
				else
					m_Ball.Select();
			}
			else
			{
				m_Ball.Select();
			}
		}
		
	for( int id=0 ; id<m_opponentN ; id++ )
	{
		if( m_Opponent[id].IsExist() )
		{
			if( m_Opponent[id].IsGrasp() )
			{
				m_Opponent[id].Drop( point2-point1 );

				if( p1 == p2 && IsPointInRect( point1, point2, m_Opponent[id].Position() ) )
				{
					if( m_Opponent[id].IsSelect() )
						m_Opponent[id].Deselect();
					else
						m_Opponent[id].Select();
				}
				else
				{
					m_Opponent[id].Select();
				}
			}
			else
			{
				if( !bCtrl )
				{
					m_Opponent[id].Deselect();
				}
			}		
		}
	}

	for( int id=0 ; id<m_robotN ; id++ )
	{
		if( m_Robot[id].IsExist() )
		{
			if( m_Robot[id].IsGrasp() )
			{
				m_Robot[id].Drop( point2-point1 );

				if( p1 == p2 && IsPointInRect( point1, point2, m_Robot[id].Position() ) )
				{
					if( m_Robot[id].IsSelect() )
						m_Robot[id].Deselect();
					else
						m_Robot[id].Select();
				}
				else
				{
					m_Robot[id].Select();
				}
			}
			else
			{
				if( !bCtrl )
				{
					m_Robot[id].Deselect();
				}
			}	
		}
	}
}

bool CGameGUI::IsPointInRect(CRect rect, CPointDouble point)
{
	rect.NormalizeRect();
	rect.InflateRect( 5, 5, 5, 5 );

	if(		(rect.left-point.x)*(point.x-rect.right) >= 0 
		&&	(rect.top-point.y)*(point.y-rect.bottom) >= 0 )
		return true;

	return false;
}

bool CGameGUI::IsPointInRect( CPointDouble p1, CPointDouble p2, CPointDouble point )
{
	double left = min(p1.x, p2.x)-0.05;
	double right = max(p1.x, p2.x)+0.05;
	double top = min(p1.y, p2.y)-0.05;
	double bottom = max(p1.y, p2.y)+0.05;

	if(		(left-point.x)*(point.x-right) >= 0 
		&&	(top-point.y)*(point.y-bottom) >= 0 )
		return true;

	return false;
}

void CGameGUI::Screen_ControlMoveTo( CPoint point, int MoveType )
{
	CPointDouble p(point);
	m_ground.ScreenToGround( p );

	for( int id=0 ; id<m_opponentN ; id++ )
	{
		if( m_Opponent[id].IsExist() )
		if( m_Opponent[id].IsSelect() )
		{
			CPointDouble direction = p - m_Opponent[id].PositionCurrent();
			//m_Opponent[id].SetVelocityCurrent(  );

			CPointDouble vel = m_Opponent[id].Acceleration() + direction*5;
			m_Opponent[id].SetVelocityCurrent( vel );
		}
	}

	if( m_Ball.IsSelect() )
	{
		CPointDouble direction = p - m_Ball.PositionCurrent();
		//m_Ball.SetVelocityCurrent( (p - m_Ball.PositionCurrent())*4 );
		CPointDouble vel = m_Ball.Acceleration() + direction;
		m_Ball.SetVelocityCurrent( vel );
		return;
	}


	int n=0;
	CPointDouble sum(0,0);

	for( int id=0 ; id<m_robotN ; id++ )
		if( m_Robot[id].IsSelect() )
		{
			sum += m_Robot[id].Position();		
			n++;
		}

	if( n>0 )
	{
		for( int id=0 ; id<m_robotN ; id++ )
		{
			if( m_Robot[id].IsExist() && m_Robot[id].IsSelect() )
			{
				CPointDouble posClick = m_Robot[id].Position() - sum/n + p;
				
				switch( MoveType )
				{
					case 0 :
						{
							m_Robot[id].MissionReset();

							CMission mission;

							mission.bTemporary = true;

							mission.text = _T("User Movement");
							mission.type = MissionType::MoveTo;
							mission.behavior.controlType = StrategyGUI_BehaviorControlType::Once;
							mission.behavior.options.move_to.destination.bBall = false;
							mission.behavior.options.move_to.destination.pos = posClick;
							mission.behavior.options.move_to.destination.error = 0.01;

							mission.posDestination = posClick;

							m_Robot[id].MissionAdd( mission );
							m_Robot[id].m_MissionSet.UpdateRecentMission( mission, 0 );
						}
						break;

					/*
					case 1 :
						{
							m_Robot[id].MissionReset();

							CMission mission;
							mission.type = CMission::MOVE_TO;
							mission.text = _T("이동");

							mission.bUseBallPosition = false;
							mission.pos = posClick;
							mission.errorPos = 0.03;

							m_Robot[id].MissionAdd( mission );
						}
						break;

					case 2 :
						{
							m_Robot[id].MissionReset();

							CMission mission;
							mission.type = CMission::MOVE_TO_DIRECTION;
							mission.text = _T("이동(방향)");

							mission.bUseBallPosition = false;
							mission.pos = posClick;
							mission.errorPos = 0.03;
							mission.posDirection = posClick+CPointDouble(1.5,0.65);
							mission.parameter[0] = 0.05;
				
							m_Robot[id].MissionAdd( mission );
						}
						break;

					case 3 :
						{
							CMission mission;
							mission.type = CMission::SPLINE_PATH;
							mission.text = _T("Spline Path");

							mission.parameter[0] = posClick.x;
							mission.parameter[1] = posClick.y;

							mission.pos = posClick;
							mission.AddPointControl( m_Robot[id].Position() );
							//mission.AddPointControl( m_Ball.Position() );
							mission.AddPointControl( posClick );
							mission.orientation = (posClick - m_Robot[id].Position()).AngleDegree();

							m_Robot[id].MissionAdd( mission );
							m_Robot[id].UpdatePath();
						}
						break;

					case 4 :
						{
							//m_Robot[id].MissionReset();
							//
							//CMission mission;
							//mission.type = CMission::MOVE_TO_BEHIND_BALL;
							//mission.text = _T("공 뒤로 이동");
							//mission.behavior = _T("");
							//mission.bUseBallPosition = false;
							//mission.pos = m_Ball.Position() - CPointDouble(-0.20, 0);
							//mission.errorPos = 0.03;
							//
							//m_Robot[id].MissionAdd( mission );
						}
						break;
					*/
				}
				
			}
		}
	}
}

void CGameGUI::Screen_ControlOrientationTo(CPoint point1, CPoint point2, bool bDrop)
{
	if( m_Ball.IsSelect() )
	{
		return;
	}

	CPointDouble p1(point1);
	CPointDouble p2(point2);

	if( (p1-p2).Distance() < 10 )
		return;

	m_ground.ScreenToGround( p1 );
	m_ground.ScreenToGround( p2 );

	for( int id=0 ; id<m_robotN ; id++ )
	{
		if( m_Robot[id].IsExist() )
		if( m_Robot[id].IsSelect() || m_Robot[id].IsGrasp() )
		{
			m_Robot[id].MissionReset();

			CMission mission;

			mission.bTemporary = true;

			mission.text = _T("User rotate");
			mission.type = MissionType::OrientationTo;
			mission.behavior.controlType = StrategyGUI_BehaviorControlType::Once;
			mission.behavior.options.orientation.orientation.bPosDirection = false;
			mission.behavior.options.orientation.orientation.direction = 5*(int)(atan2((double)(p2.y-p1.y), (double)(p2.x-p1.x)) *180.0 / M_PI /5.0);

			mission.orientationDestination = 5*(int)(atan2((double)(p2.y-p1.y), (double)(p2.x-p1.x)) *180.0 / M_PI /5.0);
			m_Robot[id].MissionAdd( mission );
			m_Robot[id].m_MissionSet.UpdateRecentMission( mission, 0 );

			if( bDrop )
				m_Robot[id].Drop();

			m_Robot[id].Select();
		}
	}
}

int CGameGUI::SelectRobotID()
{
	int result_id = -1;
	int count=0;

	for( int id=0 ; id<m_robotN ; id++ )
	{
		if( m_Robot[id].IsExist() )
		if( m_Robot[id].IsSelect() )
		{
			result_id = id;
			count++;
		}
	}

	if( count > 1 ) return -1;

	return result_id;
}

void CGameGUI::Stop()
{
	for( int id=0 ; id<m_robotN ; id++ )
	{
		m_Robot[id].Stop();
	}

	for( int id=0 ; id<m_opponentN ; id++ )
	{
		m_Opponent[id].SetVelocityCurrent( CPointDouble(0,0) );
	}
}
