import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { LawresultVO, LawresultForm, LawresultQuery } from '@/api/system/lawresult/types';

/**
 * 查询判案结果列表
 * @param query
 * @returns {*}
 */

export const listLawresult = (query?: LawresultQuery): AxiosPromise<LawresultVO[]> => {
  return request({
    url: '/system/lawresult/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询判案结果详细
 * @param id
 */
export const getLawresult = (id: string | number): AxiosPromise<LawresultVO> => {
  return request({
    url: '/system/lawresult/' + id,
    method: 'get'
  });
};

/**
 * 新增判案结果
 * @param data
 */
export const addLawresult = (data: LawresultForm) => {
  return request({
    url: '/system/lawresult',
    method: 'post',
    data: data
  });
};

/**
 * 修改判案结果
 * @param data
 */
export const updateLawresult = (data: LawresultForm) => {
  return request({
    url: '/system/lawresult',
    method: 'put',
    data: data
  });
};

/**
 * 删除判案结果
 * @param id
 */
export const delLawresult = (id: string | number | Array<string | number>) => {
  return request({
    url: '/system/lawresult/' + id,
    method: 'delete'
  });
};
