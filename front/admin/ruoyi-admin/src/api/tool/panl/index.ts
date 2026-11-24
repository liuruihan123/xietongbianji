import request from '@/utils/request';
import { AxiosPromise } from 'axios';

// 请求参数接口
export interface PanelParams {
  type: 'today' | 'week' | 'month' | 'year' | 'free';
  beginTime?: string;
  endTime?: string;
}

// 响应数据接口
export interface DashboardData {
  headerCards: {
    sales: {
      total: {
        rmb: string;
        usd: string;
      };
      today: {
        rmb: string;
        usd: string;
      };
      chartData: {
        dates: string[];
        rmb: number[];
        usd: number[];
      };
    };
    members: {
      total: string;
      paying: string;
      chartData: {
        dates: string[];
        values: number[];
      };
    };
    conversations: {
      total: string;
      today: string;
      chartData: {
        dates: string[];
        values: number[];
      };
    };
    tokens: {
      total: string;
      today: string;
      chartData: {
        dates: string[];
        values: number[];
      };
    };
  };
  rankList: Array<{
    name: string;
    value: string;
  }>;
}

// 获取面板数据的接口请求
export function getPanelData(params: PanelParams): AxiosPromise<DashboardData> {
  return request({
    url: '/system/panel/cardData',
    method: 'get',
    params
  });
}

// 使用示例：
/*
import { getPanelData } from '@/api/tool/panl';

// 按预设时间范围查询
const response = await getPanelData({
  type: 'month'
});

// 按自定义时间范围查询
const response = await getPanelData({
  type: 'free',
  beginTime: '2024-01-01 00:00:00',
  endTime: '2024-01-15 23:59:59'
});
*/
