import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { LawListVO, LawListForm, LawListQuery } from '@/api/system/lawList/types';

/**
 * 查询法律条文列表
 * @param query
 * @returns {*}
 */

export const listLawList = (query?: LawListQuery): AxiosPromise<LawListVO[]> => {
  return request({
    url: '/system/lawList/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询法律条文详细
 * @param id
 */
export const getLawList = (id: string | number): AxiosPromise<LawListVO> => {
  return request({
    url: '/system/lawList/' + id,
    method: 'get'
  });
};

/**
 * 新增法律条文
 * @param data
 */
export const addLawList = (data: LawListForm) => {
  return request({
    url: '/system/lawList',
    method: 'post',
    data: data
  });
};

/**
 * 修改法律条文
 * @param data
 */
export const updateLawList = (data: LawListForm) => {
  return request({
    url: '/system/lawList',
    method: 'put',
    data: data
  });
};

/**
 * 删除法律条文
 * @param id
 */
export const delLawList = (id: string | number | Array<string | number>) => {
  return request({
    url: '/system/lawList/' + id,
    method: 'delete'
  });
};
