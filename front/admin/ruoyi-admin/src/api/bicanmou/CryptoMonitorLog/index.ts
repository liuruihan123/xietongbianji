import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { CryptoMonitorLogVO, CryptoMonitorLogForm, CryptoMonitorLogQuery } from '@/api/bicanmou/CryptoMonitorLog/types';

/**
 * 查询监控记录列表
 * @param query
 * @returns {*}
 */

export const listCryptoMonitorLog = (query?: CryptoMonitorLogQuery): AxiosPromise<CryptoMonitorLogVO[]> => {
  return request({
    url: '/bicanmou/CryptoMonitorLog/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询监控记录详细
 * @param id
 */
export const getCryptoMonitorLog = (id: string | number): AxiosPromise<CryptoMonitorLogVO> => {
  return request({
    url: '/bicanmou/CryptoMonitorLog/' + id,
    method: 'get'
  });
};

/**
 * 新增监控记录
 * @param data
 */
export const addCryptoMonitorLog = (data: CryptoMonitorLogForm) => {
  return request({
    url: '/bicanmou/CryptoMonitorLog',
    method: 'post',
    data: data
  });
};

/**
 * 修改监控记录
 * @param data
 */
export const updateCryptoMonitorLog = (data: CryptoMonitorLogForm) => {
  return request({
    url: '/bicanmou/CryptoMonitorLog',
    method: 'put',
    data: data
  });
};

/**
 * 删除监控记录
 * @param id
 */
export const delCryptoMonitorLog = (id: string | number | Array<string | number>) => {
  return request({
    url: '/bicanmou/CryptoMonitorLog/' + id,
    method: 'delete'
  });
};
